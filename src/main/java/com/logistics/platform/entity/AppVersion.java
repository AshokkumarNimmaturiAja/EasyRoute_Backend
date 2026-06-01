package com.logistics.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVersion extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String platform; // "WEB", "ANDROID", "IOS"

    @Column(name = "version_number", nullable = false, length = 50)
    private String versionNumber; // e.g. "v1.0.0", "v2.0.0"

    @Column(name = "api_version", nullable = false, length = 10)
    private String apiVersion; // e.g. "V1", "V2"

    @Column(name = "is_mandatory_update", nullable = false)
    @Builder.Default
    private Boolean isMandatoryUpdate = false;

    @Column(nullable = false, length = 20)
    private String status; // e.g. "ACTIVE", "DEPRECATED"
}
