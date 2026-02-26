package com.jiucom.api.domain.like.service;

import com.jiucom.api.domain.build.entity.Build;
import com.jiucom.api.domain.build.repository.BuildRepository;
import com.jiucom.api.domain.comment.entity.Comment;
import com.jiucom.api.domain.comment.repository.CommentRepository;
import com.jiucom.api.domain.like.dto.response.LikeResponse;
import com.jiucom.api.domain.like.entity.ContentLike;
import com.jiucom.api.domain.like.entity.enums.LikeTargetType;
import com.jiucom.api.domain.like.repository.ContentLikeRepository;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.review.entity.Review;
import com.jiucom.api.domain.review.repository.ReviewRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final ContentLikeRepository contentLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final BuildRepository buildRepository;

    @Transactional
    public LikeResponse toggleLike(LikeTargetType targetType, Long targetId) {
        Long userId = SecurityUtil.getCurrentUserId();
        validateTargetExists(targetType, targetId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Optional<ContentLike> existing = contentLikeRepository
                .findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);

        boolean liked;
        if (existing.isPresent()) {
            contentLikeRepository.delete(existing.get());
            updateLikeCount(targetType, targetId, false);
            liked = false;
        } else {
            ContentLike contentLike = ContentLike.builder()
                    .user(user)
                    .targetType(targetType)
                    .targetId(targetId)
                    .build();
            contentLikeRepository.save(contentLike);
            updateLikeCount(targetType, targetId, true);
            liked = true;
        }

        long likeCount = contentLikeRepository.countByTargetTypeAndTargetId(targetType, targetId);
        return LikeResponse.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }

    public LikeResponse getLikeStatus(LikeTargetType targetType, Long targetId) {
        validateTargetExists(targetType, targetId);

        long likeCount = contentLikeRepository.countByTargetTypeAndTargetId(targetType, targetId);

        boolean liked = false;
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            liked = contentLikeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        } catch (GlobalException ignored) {
            // anonymous user
        }

        return LikeResponse.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }

    private void validateTargetExists(LikeTargetType targetType, Long targetId) {
        boolean exists = switch (targetType) {
            case POST -> postRepository.existsById(targetId);
            case COMMENT -> commentRepository.existsById(targetId);
            case REVIEW -> reviewRepository.existsById(targetId);
            case BUILD -> buildRepository.existsById(targetId);
        };
        if (!exists) {
            throw new GlobalException(GlobalErrorCode.LIKE_TARGET_NOT_FOUND);
        }
    }

    private void updateLikeCount(LikeTargetType targetType, Long targetId, boolean increment) {
        switch (targetType) {
            case POST -> {
                Post post = postRepository.findById(targetId).orElseThrow();
                if (increment) post.incrementLikeCount();
                else post.decrementLikeCount();
            }
            case COMMENT -> {
                Comment comment = commentRepository.findById(targetId).orElseThrow();
                if (increment) comment.incrementLikeCount();
                else comment.decrementLikeCount();
            }
            case REVIEW -> {
                Review review = reviewRepository.findById(targetId).orElseThrow();
                if (increment) review.incrementLikeCount();
                else review.decrementLikeCount();
            }
            case BUILD -> {
                Build build = buildRepository.findById(targetId).orElseThrow();
                if (increment) build.incrementLikeCount();
                else build.decrementLikeCount();
            }
        }
    }
}
