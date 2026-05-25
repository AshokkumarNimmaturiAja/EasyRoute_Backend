package com.logistics.platform.service.pricing;

import com.logistics.platform.dto.request.PricingCalculateRequest;
import com.logistics.platform.dto.response.CourierOption;
import com.logistics.platform.dto.response.PricingCalculateResponse;
import com.logistics.platform.entity.*;
import com.logistics.platform.repository.CourierRepository;
import com.logistics.platform.repository.RateCardRepository;
import com.logistics.platform.repository.SellerContractRepository;
import com.logistics.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PricingEngineService {

    private final ZoneDetectionStrategy zoneDetectionStrategy;
    private final CourierRecommendationEngine courierRecommendationEngine;
    private final CourierRepository courierRepository;
    private final RateCardRepository rateCardRepository;
    private final SellerContractRepository sellerContractRepository;
    private final UserRepository userRepository;
    private final RateCardService rateCardService;

    @Transactional(readOnly = true)
    public PricingCalculateResponse calculatePricing(PricingCalculateRequest request) {
        // Step 1: Calculate volumetric weight
        // Formula: (length * breadth * height) / 5000
        BigDecimal volume = request.getLength().multiply(request.getBreadth()).multiply(request.getHeight());
        BigDecimal volumetricWeight = volume.divide(BigDecimal.valueOf(5000), 3, RoundingMode.HALF_UP);

        // Step 2: Calculate chargeable weight
        BigDecimal chargeableWeight = request.getActualWeight().max(volumetricWeight);

        // Step 3: Detect shipping zone
        ZoneType zone = zoneDetectionStrategy.detectZone(request.getPickupPincode(), request.getDeliveryPincode());

        // Setup Seller Contract context if provided
        User seller = null;
        if (request.getSellerEmail() != null && !request.getSellerEmail().isEmpty()) {
            seller = userRepository.findByEmail(request.getSellerEmail()).orElse(null);
        }

        // Step 4: Fetch active Rate Cards for the detected Zone
        List<RateCard> rateCards = rateCardRepository.findByZoneTypeAndActiveTrue(zone);
        List<CourierOption> options = new ArrayList<>();
        List<Courier> activeCouriers = courierRepository.findByActiveTrueOrderByPriorityAsc();
        
        // Fetch Platform Revenue Split Configurations
        UserRateCard adminRate = rateCardService.getEffectiveRateForUser(null, Role.ROLE_ADMIN);
        UserRateCard driverRate = rateCardService.getEffectiveRateForUser(null, Role.ROLE_DRIVER);
        UserRateCard pickupRate = rateCardService.getEffectiveRateForUser(null, Role.ROLE_PICKUP);

        for (RateCard card : rateCards) {
            // Check weight slab
            if (chargeableWeight.compareTo(card.getWeightFrom()) >= 0 &&
                chargeableWeight.compareTo(card.getWeightTo()) <= 0) {

                Courier courier = card.getCourier();

                // Step 5: Calculate Freight
                BigDecimal freight = card.getBasePrice();
                // If there's an additional weight concept beyond base, logic could be expanded here.
                // Assuming basePrice covers the slab.

                // Step 6: COD Charges
                BigDecimal codCharge = BigDecimal.ZERO;
                if ("COD".equalsIgnoreCase(request.getPaymentMode())) {
                    BigDecimal percentageFee = request.getCodAmount()
                            .multiply(card.getCodPercentage() != null ? card.getCodPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                    BigDecimal fixedFee = card.getCodFixedFee() != null ? card.getCodFixedFee() : BigDecimal.ZERO;
                    codCharge = percentageFee.max(fixedFee);
                }

                // Step 7: Fuel Surcharge
                BigDecimal fuelCharge = BigDecimal.ZERO;
                if (card.getFuelPercentage() != null) {
                    fuelCharge = freight.multiply(card.getFuelPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                }

                // Step 8: Remote Surcharge
                BigDecimal remoteCharge = BigDecimal.ZERO;
                if (zone == ZoneType.REMOTE && card.getRemoteAreaCharge() != null) {
                    remoteCharge = card.getRemoteAreaCharge();
                }

                // Apply Seller Contract Discount if applicable
                if (seller != null) {
                    Optional<SellerContract> contractOpt = sellerContractRepository.findBySellerAndCourierId(seller, courier.getId());
                    if (contractOpt.isPresent()) {
                        SellerContract contract = contractOpt.get();
                        if (contract.getDiscountPercentage() != null) {
                            BigDecimal discount = freight.multiply(contract.getDiscountPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                            freight = freight.subtract(discount);
                        }
                    }
                }

                // Step 9: GST
                BigDecimal subTotal = freight.add(codCharge).add(fuelCharge).add(remoteCharge);
                BigDecimal gstCharge = BigDecimal.ZERO;
                if (card.getGstPercentage() != null) {
                    gstCharge = subTotal.multiply(card.getGstPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                }

                // Step 10: Final Total
                BigDecimal total = subTotal.add(gstCharge);
                
                // Step 11: Revenue Split
                BigDecimal adminMargin = BigDecimal.ZERO;
                if (adminRate != null && adminRate.getCommissionPercentage() != null) {
                    adminMargin = total.multiply(adminRate.getCommissionPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                }
                
                BigDecimal driverPayout = BigDecimal.ZERO;
                if (driverRate != null) {
                    BigDecimal driverBase = driverRate.getBaseFare() != null ? driverRate.getBaseFare() : BigDecimal.ZERO;
                    BigDecimal driverComm = driverRate.getCommissionPercentage() != null 
                        ? total.multiply(driverRate.getCommissionPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                        : BigDecimal.ZERO;
                    driverPayout = driverBase.add(driverComm);
                }
                
                BigDecimal pickupPayout = BigDecimal.ZERO;
                if (pickupRate != null && pickupRate.getFixedFee() != null) {
                    pickupPayout = pickupRate.getFixedFee();
                }

                options.add(CourierOption.builder()
                        .courierId(courier.getId())
                        .courierName(courier.getName())
                        .freight(freight.setScale(2, RoundingMode.HALF_UP))
                        .codCharge(codCharge.setScale(2, RoundingMode.HALF_UP))
                        .fuelCharge(fuelCharge.setScale(2, RoundingMode.HALF_UP))
                        .gstCharge(gstCharge.setScale(2, RoundingMode.HALF_UP))
                        .remoteCharge(remoteCharge.setScale(2, RoundingMode.HALF_UP))
                        .total(total.setScale(2, RoundingMode.HALF_UP))
                        .driverPayout(driverPayout.setScale(2, RoundingMode.HALF_UP))
                        .pickupPayout(pickupPayout.setScale(2, RoundingMode.HALF_UP))
                        .adminMargin(adminMargin.setScale(2, RoundingMode.HALF_UP))
                        .etaDays(courier.getAvgDeliveryDays() != null ? courier.getAvgDeliveryDays() : 3)
                        .build());
            }
        }

        // Apply Recommendation Engine to filter to top 3
        List<CourierOption> topOptions = courierRecommendationEngine.recommendCouriers(options, activeCouriers);

        return PricingCalculateResponse.builder()
                .zone(zone)
                .volumetricWeight(volumetricWeight)
                .chargeableWeight(chargeableWeight)
                .courierOptions(topOptions)
                .build();
    }
}
