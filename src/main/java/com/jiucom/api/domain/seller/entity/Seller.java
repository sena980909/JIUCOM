package com.jiucom.api.domain.seller.entity;

import com.jiucom.api.domain.seller.entity.enums.SellerStatus;
import com.jiucom.api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sellers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seller extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private String siteUrl;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SellerStatus status;

    @Column(nullable = false)
    @Builder.Default
    private double reliabilityScore = 0.0;
}
