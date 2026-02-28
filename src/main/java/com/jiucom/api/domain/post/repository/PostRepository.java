package com.jiucom.api.domain.post.repository;

import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    Page<Post> findByIsDeletedFalse(Pageable pageable);

    Page<Post> findByBoardTypeAndIsDeletedFalse(BoardType boardType, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String keyword, Pageable pageable);

    long countByIsDeletedFalse();
}
