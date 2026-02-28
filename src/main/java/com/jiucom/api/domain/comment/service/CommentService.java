package com.jiucom.api.domain.comment.service;

import com.jiucom.api.domain.comment.dto.request.CommentCreateRequest;
import com.jiucom.api.domain.comment.dto.request.CommentUpdateRequest;
import com.jiucom.api.domain.comment.dto.response.CommentListResponse;
import com.jiucom.api.domain.comment.dto.response.CommentResponse;
import com.jiucom.api.domain.comment.entity.Comment;
import com.jiucom.api.domain.comment.repository.CommentRepository;
import com.jiucom.api.domain.notification.entity.enums.NotificationType;
import com.jiucom.api.domain.notification.service.NotificationService;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.RedisUtil;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RedisUtil redisUtil;

    public CommentListResponse getComments(Long postId, int page, int size) {
        // Check cache
        CommentListResponse cached = redisUtil.getCachedCommentList(postId, page, size);
        if (cached != null) {
            return cached;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Comment> rootComments = commentRepository.findByPostIdAndParentIsNullAndIsDeletedFalse(postId, pageable);

        List<CommentResponse> commentResponses = rootComments.getContent().stream()
                .map(comment -> {
                    List<CommentResponse> replies = commentRepository
                            .findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(comment.getId())
                            .stream()
                            .map(CommentResponse::from)
                            .toList();
                    return CommentResponse.of(comment, replies);
                })
                .toList();

        CommentListResponse response = CommentListResponse.builder()
                .comments(commentResponses)
                .totalPages(rootComments.getTotalPages())
                .totalElements(rootComments.getTotalElements())
                .currentPage(page)
                .build();

        // Store in cache
        redisUtil.cacheCommentList(postId, page, size, response);

        return response;
    }

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .filter(c -> !c.isDeleted())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .parent(parent)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);
        post.incrementCommentCount();

        // Invalidate caches
        redisUtil.evictCommentListsForPost(postId);
        redisUtil.evictPostDetail(postId);

        // Send notification to post author (if not self-comment)
        if (!post.getAuthor().getId().equals(userId)) {
            String title = "새 댓글이 달렸습니다";
            String message = user.getNickname() + "님이 \"" + post.getTitle() + "\" 게시글에 댓글을 남겼습니다.";
            String linkUrl = "/posts/" + postId;
            notificationService.sendNotification(
                    post.getAuthor().getId(), NotificationType.COMMENT_REPLY, title, message, linkUrl);
        }

        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Comment comment = commentRepository.findById(commentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.COMMENT_NOT_AUTHOR);
        }

        comment.updateContent(request.getContent());

        // Invalidate caches
        Long postId = comment.getPost().getId();
        redisUtil.evictCommentListsForPost(postId);

        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Comment comment = commentRepository.findById(commentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.COMMENT_NOT_AUTHOR);
        }

        Long postId = comment.getPost().getId();

        comment.softDelete();
        comment.getPost().decrementCommentCount();

        // Invalidate caches
        redisUtil.evictCommentListsForPost(postId);
        redisUtil.evictPostDetail(postId);
    }
}
