package com.jiucom.api.domain.review.controller;

import com.jiucom.api.domain.review.dto.request.ReviewCreateRequest;
import com.jiucom.api.domain.review.dto.request.ReviewUpdateRequest;
import com.jiucom.api.domain.review.dto.response.ReviewResponse;
import com.jiucom.api.domain.review.service.ReviewService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "부품 리뷰 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "부품 리뷰 목록 조회")
    @GetMapping("/parts/{partId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @PathVariable Long partId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ReviewResponse> reviews = reviewService.getReviews(partId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @Operation(summary = "리뷰 작성")
    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(review));
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewResponse review = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.ok(review));
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
