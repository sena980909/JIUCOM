package com.jiucom.api.domain.post.repository;

import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByIsDeletedFalse(Pageable pageable);

    Page<Post> findByBoardTypeAndIsDeletedFalse(BoardType boardType, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String keyword, Pageable pageable);

    long countByIsDeletedFalse();
}
