package com.logistics.platform.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.logistics.platform.dto.response.CourierOption;
import com.logistics.platform.entity.Courier;

@Component
public class CourierRecommendationEngine {

    public List<CourierOption> recommendCouriers(List<CourierOption> options, List<Courier> couriers) {
        // Score formula: (price_score * 0.4) + (speed_score * 0.2) + (success_score * 0.3) + (rto_score * 0.1)
        
        if (options.isEmpty()) return options;

        BigDecimal maxPrice = options.stream().map(CourierOption::getTotal).max(Comparator.naturalOrder()).orElse(BigDecimal.ONE);
        Integer maxDays = options.stream().map(CourierOption::getEtaDays).max(Comparator.naturalOrder()).orElse(1);

        for (CourierOption option : options) {
            Courier courier = couriers.stream()
                    .filter(c -> c.getId().equals(option.getCourierId()))
                    .findFirst().orElse(null);

            if (courier == null) continue;

            // Price score (lower is better, invert)
            BigDecimal priceScore = BigDecimal.ONE.subtract(
                    option.getTotal().divide(maxPrice, 4, RoundingMode.HALF_UP)
            ).multiply(BigDecimal.valueOf(100));

            // Speed score (lower is better, invert)
            BigDecimal speedScore = BigDecimal.ONE.subtract(
                    BigDecimal.valueOf(option.getEtaDays()).divide(BigDecimal.valueOf(maxDays), 4, RoundingMode.HALF_UP)
            ).multiply(BigDecimal.valueOf(100));

            // Success score (higher is better)
            BigDecimal successScore = courier.getDeliverySuccessRate() != null ? 
                    courier.getDeliverySuccessRate() : BigDecimal.valueOf(80); // Default 80%

            // RTO score (lower is better, invert)
            BigDecimal rtoPercentage = courier.getRtoPercentage() != null ? 
                    courier.getRtoPercentage() : BigDecimal.valueOf(20); // Default 20%
            BigDecimal rtoScore = BigDecimal.valueOf(100).subtract(rtoPercentage);

            BigDecimal finalScore = (priceScore.multiply(BigDecimal.valueOf(0.4)))
                    .add(speedScore.multiply(BigDecimal.valueOf(0.2)))
                    .add(successScore.multiply(BigDecimal.valueOf(0.3)))
                    .add(rtoScore.multiply(BigDecimal.valueOf(0.1)));

            option.setScore(finalScore);
        }

        // Return top 3 options sorted by score descending
        return options.stream()
                .sorted(Comparator.comparing(CourierOption::getScore).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
}
