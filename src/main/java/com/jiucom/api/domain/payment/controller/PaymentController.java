package com.jiucom.api.domain.payment.controller;

import com.jiucom.api.domain.payment.dto.request.PaymentCreateRequest;
import com.jiucom.api.domain.payment.dto.response.PaymentResponse;
import com.jiucom.api.domain.payment.service.PaymentService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 요청", description = "실결제 연동 전까지 '구매나 결제는 아직 준비가 안되었습니다' 응답 반환")
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.created(paymentService.createPayment(request)));
    }

    @Operation(summary = "모의 결제 (테스트용)", description = "실제 PG 연동 없이 결제 프로세스를 시뮬레이션합니다")
    @PostMapping("/mock")
    public ResponseEntity<ApiResponse<PaymentResponse>> createMockPayment(
            @Valid @RequestBody PaymentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.created(paymentService.createMockPayment(request)));
    }

    @Operation(summary = "내 결제 내역 조회")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getMyPayments(page, size)));
    }

    @Operation(summary = "결제 상세 조회")
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentDetail(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentDetail(paymentId)));
    }

    @Operation(summary = "결제 취소", description = "실결제 연동 전까지 '구매나 결제는 아직 준비가 안되었습니다' 응답 반환")
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.cancelPayment(paymentId)));
    }
}
