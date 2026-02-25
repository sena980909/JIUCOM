package com.jiucom.api.domain.payment.dto.response;

import com.jiucom.api.domain.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

    private Long id;
    private String orderNumber;
    private Long buildId;
    private String buildName;
    private Integer totalAmount;
    private String paymentMethod;
    private String status;
    private String failReason;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderNumber(payment.getOrderNumber())
                .buildId(payment.getBuild() != null ? payment.getBuild().getId() : null)
                .buildName(payment.getBuild() != null ? payment.getBuild().getName() : null)
                .totalAmount(payment.getTotalAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .failReason(payment.getFailReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
