package com.jiucom.api.domain.part.dto.request;

import com.jiucom.api.domain.part.entity.enums.PartCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartUpdateRequest {

    private String name;

    private PartCategory category;

    private String manufacturer;

    private String modelNumber;

    private String imageUrl;

    private String specs;

    private Integer lowestPrice;

    private Integer highestPrice;
}
