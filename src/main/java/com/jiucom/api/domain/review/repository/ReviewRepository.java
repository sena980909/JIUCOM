package com.jiucom.api.domain.review.repository;

import com.jiucom.api.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByPartIdAndIsDeletedFalse(Long partId, Pageable pageable);

    boolean existsByAuthorIdAndPartId(Long authorId, Long partId);
}
