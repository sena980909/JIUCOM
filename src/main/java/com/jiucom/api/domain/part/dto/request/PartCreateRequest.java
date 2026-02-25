package com.jiucom.api.domain.part.dto.request;

import com.jiucom.api.domain.part.entity.enums.PartCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartCreateRequest {

    @NotBlank(message = "부품명은 필수입니다.")
    private String name;

    @NotNull(message = "카테고리는 필수입니다.")
    private PartCategory category;

    @NotBlank(message = "제조사는 필수입니다.")
    private String manufacturer;

    private String modelNumber;

    private String imageUrl;

    private String specs;

    private Integer lowestPrice;

    private Integer highestPrice;
}
