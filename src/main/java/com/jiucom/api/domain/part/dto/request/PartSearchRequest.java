package com.jiucom.api.domain.part.dto.request;

import com.jiucom.api.domain.part.entity.enums.PartCategory;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartSearchRequest {

    private String keyword;

    private PartCategory category;

    @Min(value = 0, message = "최소 가격은 0 이상이어야 합니다.")
    private Integer minPrice;

    @Min(value = 0, message = "최대 가격은 0 이상이어야 합니다.")
    private Integer maxPrice;

    private String sort;

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private int size = 20;
}
