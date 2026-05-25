package com.logistics.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "remote_pincodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemotePincode extends BaseEntity {

    @Column(nullable = false, length = 10, unique = true)
    private String pincode;

    @Column(nullable = false, length = 100)
    private String state;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
