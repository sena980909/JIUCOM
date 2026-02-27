package com.jiucom.api.domain.admin.controller;

import com.jiucom.api.domain.admin.dto.response.DashboardResponse;
import com.jiucom.api.domain.admin.service.AdminService;
import com.jiucom.api.domain.part.dto.request.PartCreateRequest;
import com.jiucom.api.domain.part.dto.request.PartUpdateRequest;
import com.jiucom.api.domain.part.dto.response.PartDetailResponse;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.service.PartService;
import com.jiucom.api.global.naver.NaverShoppingService;
import com.jiucom.api.global.naver.NaverShoppingService.NaverImportResult;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PartService partService;
    private final AdminService adminService;
    private final NaverShoppingService naverShoppingService;

    @Operation(summary = "대시보드 통계")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    @Operation(summary = "사용자 관리")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<DashboardResponse.RecentUserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DashboardResponse.RecentUserResponse> users = adminService.getUsers(page, size);
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @Operation(summary = "부품 등록")
    @PostMapping("/parts")
    public ResponseEntity<ApiResponse<PartDetailResponse>> createPart(
            @Valid @RequestBody PartCreateRequest request) {
        PartDetailResponse response = partService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "부품 수정")
    @PutMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<PartDetailResponse>> updatePart(
            @PathVariable Long partId,
            @Valid @RequestBody PartUpdateRequest request) {
        PartDetailResponse response = partService.updatePart(partId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "부품 삭제")
    @DeleteMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<Void>> deletePart(@PathVariable Long partId) {
        partService.deletePart(partId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "네이버 쇼핑 데이터 전체 임포트", description = "네이버 쇼핑 API에서 컴퓨터 부품 데이터를 가져옵니다 (CPU, GPU, RAM, SSD, 메인보드, 파워, 케이스, 쿨러)")
    @PostMapping("/naver-import")
    public ResponseEntity<ApiResponse<NaverImportResult>> importFromNaver() {
        NaverImportResult result = naverShoppingService.importAll();
        if (result.success()) {
            return ResponseEntity.ok(ApiResponse.ok(result));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("JIUCOM-A010", result.message()));
    }

    @Operation(summary = "네이버 쇼핑 카테고리별 임포트", description = "특정 카테고리의 부품 데이터만 가져옵니다")
    @PostMapping("/naver-import/{category}")
    public ResponseEntity<ApiResponse<NaverImportResult>> importCategoryFromNaver(
            @PathVariable String category) {
        try {
            PartCategory partCategory = PartCategory.valueOf(category.toUpperCase());
            NaverImportResult result = naverShoppingService.importCategory(partCategory);
            if (result.success()) {
                return ResponseEntity.ok(ApiResponse.ok(result));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("JIUCOM-A010", result.message()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("JIUCOM-A011", "잘못된 카테고리: " + category));
        }
    }
}
