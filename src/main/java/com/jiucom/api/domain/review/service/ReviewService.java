package com.jiucom.api.domain.review.service;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.review.dto.request.ReviewCreateRequest;
import com.jiucom.api.domain.review.dto.request.ReviewUpdateRequest;
import com.jiucom.api.domain.review.dto.response.ReviewResponse;
import com.jiucom.api.domain.review.entity.Review;
import com.jiucom.api.domain.review.repository.ReviewRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    public Page<ReviewResponse> getReviews(Long partId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewRepository.findByPartIdAndIsDeletedFalse(partId, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Part part = partRepository.findById(request.getPartId())
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        if (reviewRepository.existsByAuthorIdAndPartId(userId, request.getPartId())) {
            throw new GlobalException(GlobalErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .author(user)
                .part(part)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        reviewRepository.save(review);
        return ReviewResponse.from(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.REVIEW_NOT_FOUND));

        if (!review.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.REVIEW_NOT_AUTHOR);
        }

        if (request.getRating() != null) {
            review.updateRating(request.getRating());
        }
        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }

        return ReviewResponse.from(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.REVIEW_NOT_FOUND));

        if (!review.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.REVIEW_NOT_AUTHOR);
        }

        review.softDelete();
    }
}
