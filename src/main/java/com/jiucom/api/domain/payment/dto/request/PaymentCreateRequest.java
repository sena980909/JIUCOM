package com.jiucom.api.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "견적 ID는 필수입니다.")
    private Long buildId;

    @NotBlank(message = "결제 수단은 필수입니다.")
    private String paymentMethod; // CREDIT_CARD, KAKAO_PAY, etc.
}
