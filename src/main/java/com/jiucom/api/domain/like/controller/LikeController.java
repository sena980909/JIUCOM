package com.jiucom.api.domain.like.controller;

import com.jiucom.api.domain.like.dto.response.LikeResponse;
import com.jiucom.api.domain.like.entity.enums.LikeTargetType;
import com.jiucom.api.domain.like.service.LikeService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Tag(name = "Like", description = "좋아요 API")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{targetType}/{targetId}")
    @Operation(summary = "좋아요 토글", description = "좋아요 추가/취소 (토글)")
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
            @PathVariable LikeTargetType targetType,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(ApiResponse.ok(likeService.toggleLike(targetType, targetId)));
    }

    @GetMapping("/{targetType}/{targetId}")
    @Operation(summary = "좋아요 상태 조회", description = "좋아요 여부 + 좋아요 수 조회")
    public ResponseEntity<ApiResponse<LikeResponse>> getLikeStatus(
            @PathVariable LikeTargetType targetType,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(ApiResponse.ok(likeService.getLikeStatus(targetType, targetId)));
    }
}
