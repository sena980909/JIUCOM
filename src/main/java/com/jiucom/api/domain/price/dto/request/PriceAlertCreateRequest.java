package com.jiucom.api.domain.price.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PriceAlertCreateRequest {

    @NotNull(message = "부품 ID는 필수입니다.")
    private Long partId;

    @NotNull(message = "목표 가격은 필수입니다.")
    @Positive(message = "목표 가격은 양수여야 합니다.")
    private Integer targetPrice;
}
