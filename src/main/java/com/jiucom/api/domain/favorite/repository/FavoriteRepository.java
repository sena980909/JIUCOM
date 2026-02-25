package com.jiucom.api.domain.favorite.repository;

import com.jiucom.api.domain.favorite.entity.Favorite;
import com.jiucom.api.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Page<Favorite> findByUser(User user, Pageable pageable);

    Optional<Favorite> findByUserIdAndPartId(Long userId, Long partId);

    boolean existsByUserIdAndPartId(Long userId, Long partId);
}
