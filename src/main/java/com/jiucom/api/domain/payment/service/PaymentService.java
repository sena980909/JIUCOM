package com.jiucom.api.domain.payment.service;

import com.jiucom.api.domain.build.entity.Build;
import com.jiucom.api.domain.build.repository.BuildRepository;
import com.jiucom.api.domain.payment.dto.request.PaymentCreateRequest;
import com.jiucom.api.domain.payment.dto.response.PaymentResponse;
import com.jiucom.api.domain.payment.entity.Payment;
import com.jiucom.api.domain.payment.entity.enums.PaymentMethod;
import com.jiucom.api.domain.payment.repository.PaymentRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BuildRepository buildRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayService paymentGatewayService;

    /**
     * 결제 요청 (모의 모드에서는 차단)
     */
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        // 실결제 가능 여부 체크
        if (!paymentGatewayService.isLive()) {
            throw new GlobalException(GlobalErrorCode.PAYMENT_NOT_READY);
        }

        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Build build = buildRepository.findById(request.getBuildId())
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.BUILD_NOT_FOUND));

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }

        String orderNumber = generateOrderNumber();

        Payment payment = Payment.builder()
                .orderNumber(orderNumber)
                .user(user)
                .build(build)
                .totalAmount(build.getTotalPrice())
                .paymentMethod(method)
                .build();

        paymentRepository.save(payment);

        // PG 결제 요청
        String pgTxId = paymentGatewayService.requestPayment(
                orderNumber, build.getTotalPrice(), method);

        if (pgTxId != null) {
            payment.complete(pgTxId);
        } else {
            payment.fail("PG사 결제 실패");
        }

        return PaymentResponse.from(payment);
    }

    /**
     * 모의 결제 (테스트용 - 실제 PG 연동 없이 결제 프로세스 시뮬레이션)
     */
    @Transactional
    public PaymentResponse createMockPayment(PaymentCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Build build = buildRepository.findById(request.getBuildId())
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.BUILD_NOT_FOUND));

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
        }

        String orderNumber = generateOrderNumber();

        Payment payment = Payment.builder()
                .orderNumber(orderNumber)
                .user(user)
                .build(build)
                .totalAmount(build.getTotalPrice())
                .paymentMethod(method)
                .build();

        paymentRepository.save(payment);

        // 모의 PG 결제
        String mockTxId = paymentGatewayService.requestPayment(
                orderNumber, build.getTotalPrice(), method);
        payment.complete(mockTxId);

        log.info("[MOCK] 모의 결제 완료 - 주문번호: {}, 금액: {}", orderNumber, build.getTotalPrice());
        return PaymentResponse.from(payment);
    }

    public Page<PaymentResponse> getMyPayments(int page, int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        return paymentRepository.findByUserAndIsDeletedFalse(user,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(PaymentResponse::from);
    }

    public PaymentResponse getPaymentDetail(Long paymentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.RESOURCE_NOT_FOUND));

        if (!payment.getUser().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.FORBIDDEN);
        }

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        if (!paymentGatewayService.isLive()) {
            throw new GlobalException(GlobalErrorCode.PAYMENT_NOT_READY);
        }

        Long userId = SecurityUtil.getCurrentUserId();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.RESOURCE_NOT_FOUND));

        if (!payment.getUser().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.FORBIDDEN);
        }

        boolean cancelled = paymentGatewayService.cancelPayment(
                payment.getPgTransactionId(), payment.getTotalAmount());

        if (cancelled) {
            payment.cancel();
        }

        return PaymentResponse.from(payment);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "JIU-" + timestamp + "-" + uuid;
    }
}
