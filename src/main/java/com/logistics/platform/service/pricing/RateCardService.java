package com.logistics.platform.service.pricing;

import com.logistics.platform.entity.Role;
import com.logistics.platform.entity.UserRateCard;
import com.logistics.platform.repository.UserRateCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateCardService {

    private final UserRateCardRepository userRateCardRepository;

    public List<UserRateCard> getAllGlobalRates() {
        return userRateCardRepository.findByUserIdIsNullAndActiveTrue();
    }

    public UserRateCard getEffectiveRateForUser(UUID userId, Role role) {
        // Check for specific user override
        if (userId != null) {
            Optional<UserRateCard> override = userRateCardRepository.findByUserIdAndActiveTrue(userId);
            if (override.isPresent()) {
                return override.get();
            }
        }
        
        // Fallback to global rate for that role
        List<UserRateCard> globalRates = userRateCardRepository.findByTargetRoleAndActiveTrue(role);
        return globalRates.isEmpty() ? null : globalRates.get(0);
    }
    
    public UserRateCard saveRateCard(UserRateCard rateCard) {
        return userRateCardRepository.save(rateCard);
    }
}
