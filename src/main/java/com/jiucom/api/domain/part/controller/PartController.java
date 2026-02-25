package com.jiucom.api.domain.part.controller;

import com.jiucom.api.domain.part.dto.request.PartSearchRequest;
import com.jiucom.api.domain.part.dto.response.PartDetailResponse;
import com.jiucom.api.domain.part.dto.response.PartListResponse;
import com.jiucom.api.domain.part.service.PartService;
import com.jiucom.api.global.response.ApiResponse;
import com.jiucom.api.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Part", description = "부품 API")
@RestController
@RequestMapping("/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @Operation(summary = "부품 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PartListResponse>>> getParts(
            @Valid @ModelAttribute PartSearchRequest request) {
        PageResponse<PartListResponse> response = partService.searchParts(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "부품 상세 조회")
    @GetMapping("/{partId}")
    public ResponseEntity<ApiResponse<PartDetailResponse>> getPart(@PathVariable Long partId) {
        PartDetailResponse response = partService.getPartDetail(partId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "부품 카테고리 목록")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = partService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
