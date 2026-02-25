package com.jiucom.api.domain.seller.controller;

import com.jiucom.api.domain.seller.service.SellerService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Seller", description = "판매처 API")
@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "판매처 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getSellers() {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
