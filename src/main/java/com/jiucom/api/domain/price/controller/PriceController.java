package com.jiucom.api.domain.price.controller;

import com.jiucom.api.domain.price.service.PriceService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Price", description = "가격 비교 API")
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @Operation(summary = "부품 가격 비교 조회")
    @GetMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<?>> getPriceComparison(@PathVariable Long partId) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "부품 가격 이력 조회")
    @GetMapping("/parts/{partId}/history")
    public ResponseEntity<ApiResponse<?>> getPriceHistory(
            @PathVariable Long partId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
