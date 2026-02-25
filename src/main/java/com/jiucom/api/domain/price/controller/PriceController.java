package com.jiucom.api.domain.price.controller;

import com.jiucom.api.domain.price.dto.request.PriceAlertCreateRequest;
import com.jiucom.api.domain.price.dto.response.PriceAlertResponse;
import com.jiucom.api.domain.price.dto.response.PriceComparisonResponse;
import com.jiucom.api.domain.price.dto.response.PriceHistoryResponse;
import com.jiucom.api.domain.price.service.PriceAlertService;
import com.jiucom.api.domain.price.service.PriceService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Price", description = "가격 비교 API")
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;
    private final PriceAlertService priceAlertService;

    @Operation(summary = "부품 가격 비교 조회")
    @GetMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<PriceComparisonResponse>> getPriceComparison(
            @PathVariable Long partId) {
        return ResponseEntity.ok(ApiResponse.ok(priceService.getPriceComparison(partId)));
    }

    @Operation(summary = "부품 가격 이력 조회")
    @GetMapping("/parts/{partId}/history")
    public ResponseEntity<ApiResponse<PriceHistoryResponse>> getPriceHistory(
            @PathVariable Long partId,
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(ApiResponse.ok(priceService.getPriceHistory(partId, period)));
    }

    @Operation(summary = "가격 알림 등록")
    @PostMapping("/alerts")
    public ResponseEntity<ApiResponse<PriceAlertResponse>> createAlert(
            @Valid @RequestBody PriceAlertCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.created(priceAlertService.createAlert(request)));
    }

    @Operation(summary = "내 가격 알림 목록 조회")
    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<PriceAlertResponse>>> getMyAlerts() {
        return ResponseEntity.ok(ApiResponse.ok(priceAlertService.getMyAlerts()));
    }

    @Operation(summary = "가격 알림 해제")
    @DeleteMapping("/alerts/{alertId}")
    public ResponseEntity<ApiResponse<Void>> deactivateAlert(@PathVariable Long alertId) {
        priceAlertService.deactivateAlert(alertId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
