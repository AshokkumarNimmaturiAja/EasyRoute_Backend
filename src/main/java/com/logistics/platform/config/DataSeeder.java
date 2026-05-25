package com.logistics.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.logistics.platform.entity.Role;
import com.logistics.platform.entity.User;
import com.logistics.platform.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedUserRateCards();
        seedCouriersAndRateCards();
    }

    @Autowired
    private com.logistics.platform.repository.UserRateCardRepository userRateCardRepository;

    private void seedUserRateCards() {
        if (userRateCardRepository.count() > 0) return;

        System.out.println("DataSeeder: Seeding default User Rate Cards...");

        java.util.List<com.logistics.platform.entity.UserRateCard> defaultCards = new java.util.ArrayList<>();

        // Default Admin Margin (e.g. 15% commission)
        defaultCards.add(com.logistics.platform.entity.UserRateCard.builder()
                .targetRole(com.logistics.platform.entity.Role.ROLE_ADMIN)
                .name("Global Platform Margin")
                .commissionPercentage(java.math.BigDecimal.valueOf(15.0))
                .active(true)
                .build());

        // Default Driver Pay (e.g. 50% commission, or per km rate. Let's do 50% for simplicity, plus base pay)
        defaultCards.add(com.logistics.platform.entity.UserRateCard.builder()
                .targetRole(com.logistics.platform.entity.Role.ROLE_DRIVER)
                .name("Global Driver Pay")
                .baseFare(java.math.BigDecimal.valueOf(100.0))
                .commissionPercentage(java.math.BigDecimal.valueOf(50.0))
                .active(true)
                .build());

        // Default Pickup Partner (e.g. fixed fee of 50 per parcel)
        defaultCards.add(com.logistics.platform.entity.UserRateCard.builder()
                .targetRole(com.logistics.platform.entity.Role.ROLE_PICKUP)
                .name("Global Pickup Partner Fee")
                .fixedFee(java.math.BigDecimal.valueOf(50.0))
                .active(true)
                .build());

        // Default Customer (e.g. loyalty discount base fare reduction, or just standard 0% commission)
        defaultCards.add(com.logistics.platform.entity.UserRateCard.builder()
                .targetRole(com.logistics.platform.entity.Role.ROLE_CUSTOMER)
                .name("Global Customer Plan")
                .baseFare(java.math.BigDecimal.valueOf(0.0))
                .commissionPercentage(java.math.BigDecimal.valueOf(0.0))
                .fixedFee(java.math.BigDecimal.valueOf(0.0))
                .active(true)
                .build());

        userRateCardRepository.saveAll(defaultCards);
    }

    private void seedAdminUser() {
        String adminEmail = "admin@easyroute.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            System.out.println("DataSeeder: Seeding default super administrator user...");
            User admin = User.builder()
                    .name("Super Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .phone("1000000000")
                    .role(Role.ROLE_ADMIN)
                    .isActive(true)
                    .emailVerified(true)
                    .build();
            userRepository.save(admin);
            System.out.println("DataSeeder: Default administrator account seeded successfully!");
            System.out.println("  - Email: " + adminEmail);
            System.out.println("  - Password: admin123");
        } else {
            System.out.println("DataSeeder: Admin user already exists. Skipping seeding.");
        }
    }

    @Autowired
    private com.logistics.platform.repository.CourierRepository courierRepository;

    @Autowired
    private com.logistics.platform.repository.RateCardRepository rateCardRepository;

    private void seedCouriersAndRateCards() {
        if (courierRepository.count() > 0) return;

        System.out.println("DataSeeder: Seeding couriers and rate cards...");
        
        com.logistics.platform.entity.Courier delhivery = com.logistics.platform.entity.Courier.builder()
                .name("Delhivery")
                .active(true)
                .priority(1)
                .deliverySuccessRate(java.math.BigDecimal.valueOf(92.5))
                .avgDeliveryDays(3)
                .rtoPercentage(java.math.BigDecimal.valueOf(15.0))
                .build();
                
        com.logistics.platform.entity.Courier xpressbees = com.logistics.platform.entity.Courier.builder()
                .name("Xpressbees")
                .active(true)
                .priority(2)
                .deliverySuccessRate(java.math.BigDecimal.valueOf(88.0))
                .avgDeliveryDays(4)
                .rtoPercentage(java.math.BigDecimal.valueOf(18.0))
                .build();
                
        courierRepository.saveAll(java.util.List.of(delhivery, xpressbees));

        java.util.List<com.logistics.platform.entity.RateCard> cards = new java.util.ArrayList<>();
        
        // Delhivery Local
        cards.add(com.logistics.platform.entity.RateCard.builder()
                .courier(delhivery)
                .zoneType(com.logistics.platform.entity.ZoneType.LOCAL)
                .weightFrom(java.math.BigDecimal.ZERO)
                .weightTo(java.math.BigDecimal.valueOf(500))
                .basePrice(java.math.BigDecimal.valueOf(45))
                .additionalWeightPrice(java.math.BigDecimal.valueOf(45))
                .codFixedFee(java.math.BigDecimal.valueOf(40))
                .codPercentage(java.math.BigDecimal.valueOf(1.5))
                .fuelPercentage(java.math.BigDecimal.valueOf(10))
                .gstPercentage(java.math.BigDecimal.valueOf(18))
                .active(true)
                .build());

        // Delhivery National
        cards.add(com.logistics.platform.entity.RateCard.builder()
                .courier(delhivery)
                .zoneType(com.logistics.platform.entity.ZoneType.NATIONAL)
                .weightFrom(java.math.BigDecimal.ZERO)
                .weightTo(java.math.BigDecimal.valueOf(500))
                .basePrice(java.math.BigDecimal.valueOf(75))
                .additionalWeightPrice(java.math.BigDecimal.valueOf(75))
                .codFixedFee(java.math.BigDecimal.valueOf(40))
                .codPercentage(java.math.BigDecimal.valueOf(1.5))
                .fuelPercentage(java.math.BigDecimal.valueOf(10))
                .gstPercentage(java.math.BigDecimal.valueOf(18))
                .active(true)
                .build());

        // Xpressbees Local
        cards.add(com.logistics.platform.entity.RateCard.builder()
                .courier(xpressbees)
                .zoneType(com.logistics.platform.entity.ZoneType.LOCAL)
                .weightFrom(java.math.BigDecimal.ZERO)
                .weightTo(java.math.BigDecimal.valueOf(500))
                .basePrice(java.math.BigDecimal.valueOf(35))
                .additionalWeightPrice(java.math.BigDecimal.valueOf(35))
                .codFixedFee(java.math.BigDecimal.valueOf(30))
                .codPercentage(java.math.BigDecimal.valueOf(1.8))
                .fuelPercentage(java.math.BigDecimal.valueOf(12))
                .gstPercentage(java.math.BigDecimal.valueOf(18))
                .active(true)
                .build());
                
        // Xpressbees National
        cards.add(com.logistics.platform.entity.RateCard.builder()
                .courier(xpressbees)
                .zoneType(com.logistics.platform.entity.ZoneType.NATIONAL)
                .weightFrom(java.math.BigDecimal.ZERO)
                .weightTo(java.math.BigDecimal.valueOf(500))
                .basePrice(java.math.BigDecimal.valueOf(65))
                .additionalWeightPrice(java.math.BigDecimal.valueOf(65))
                .codFixedFee(java.math.BigDecimal.valueOf(30))
                .codPercentage(java.math.BigDecimal.valueOf(1.8))
                .fuelPercentage(java.math.BigDecimal.valueOf(12))
                .gstPercentage(java.math.BigDecimal.valueOf(18))
                .active(true)
                .build());

        rateCardRepository.saveAll(cards);
    }
}
