package com.logistics.platform.service;

import com.logistics.platform.entity.UiMetadata;
import com.logistics.platform.repository.UiMetadataRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UiMetadataService {

    private final UiMetadataRepository uiMetadataRepository;

    @PostConstruct
    @Transactional
    public void init() {
        initCategory("TRUCK_TYPES", "[\"MINI\", \"MEDIUM\", \"LARGE\", \"HEAVY\", \"REFRIGERATED\"]");
        initCategory("ROLES", "[\"ADMIN\", \"DRIVER\", \"CUSTOMER\"]");
        initCategory("PRIVACY_POLICY", "{\"text\": \"Default Privacy Policy content goes here.\"}");
    }

    private void initCategory(String category, String defaultValue) {
        if (uiMetadataRepository.findByCategory(category).isEmpty()) {
            UiMetadata metadata = UiMetadata.builder()
                    .category(category)
                    .value(defaultValue)
                    .build();
            uiMetadataRepository.save(metadata);
        }
    }
}
