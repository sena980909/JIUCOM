package com.jiucom.api.domain.comment.repository;

import com.jiucom.api.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    Page<Comment> findByPostIdAndParentIsNullAndIsDeletedFalse(Long postId, Pageable pageable);
}
