package com.jiucom.api.domain.part.entity;

import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Part extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartCategory category;

    @Column(nullable = false, length = 100)
    private String manufacturer;

    @Column(length = 100)
    private String modelNumber;

    private String imageUrl;

    @Column(columnDefinition = "JSON")
    private String specs;

    private Integer lowestPrice;

    private Integer highestPrice;

    @Column(columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer popularityScore = 0;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCategory(PartCategory category) {
        this.category = category;
    }

    public void updateManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void updateSpecs(String specs) {
        this.specs = specs;
    }

    public void updatePriceRange(Integer lowestPrice, Integer highestPrice) {
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
    }
}
