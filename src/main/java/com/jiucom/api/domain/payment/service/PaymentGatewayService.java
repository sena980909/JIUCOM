package com.jiucom.api.domain.payment.service;

import com.jiucom.api.domain.payment.entity.enums.PaymentMethod;

/**
 * PG(Payment Gateway) 연동 인터페이스.
 * 향후 실제 PG사(토스페이먼츠, 아임포트 등) 연동 시 이 인터페이스를 구현합니다.
 */
public interface PaymentGatewayService {

    /**
     * 결제 요청
     * @return PG사 거래 ID (성공 시), null (실패 시)
     */
    String requestPayment(String orderNumber, Integer amount, PaymentMethod method);

    /**
     * 결제 취소/환불 요청
     */
    boolean cancelPayment(String pgTransactionId, Integer amount);

    /**
     * 실결제 가능 여부
     */
    boolean isLive();
}
