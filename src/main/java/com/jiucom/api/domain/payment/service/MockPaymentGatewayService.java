package com.jiucom.api.domain.payment.service;

import com.jiucom.api.domain.payment.entity.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 모의 결제 게이트웨이.
 * 사업자 등록번호 확보 후 실제 PG사 연동 구현체로 교체 예정.
 * isLive()가 false를 반환하므로 PaymentService에서 실결제를 차단합니다.
 */
@Slf4j
@Service
public class MockPaymentGatewayService implements PaymentGatewayService {

    @Override
    public String requestPayment(String orderNumber, Integer amount, PaymentMethod method) {
        // 모의 결제: 항상 성공하는 가짜 거래 ID 반환
        String mockTxId = "MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[MOCK PG] 결제 요청 - 주문번호: {}, 금액: {}, 수단: {}, 모의거래ID: {}",
                orderNumber, amount, method, mockTxId);
        return mockTxId;
    }

    @Override
    public boolean cancelPayment(String pgTransactionId, Integer amount) {
        log.info("[MOCK PG] 결제 취소 - 거래ID: {}, 금액: {}", pgTransactionId, amount);
        return true;
    }

    @Override
    public boolean isLive() {
        // 모의 모드: 실결제 불가
        return false;
    }
}
