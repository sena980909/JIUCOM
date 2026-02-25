package com.jiucom.api.domain.admin.controller;

import com.jiucom.api.domain.admin.dto.response.DashboardResponse;
import com.jiucom.api.domain.admin.service.AdminService;
import com.jiucom.api.domain.part.dto.request.PartCreateRequest;
import com.jiucom.api.domain.part.dto.request.PartUpdateRequest;
import com.jiucom.api.domain.part.dto.response.PartDetailResponse;
import com.jiucom.api.domain.part.service.PartService;
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
}
