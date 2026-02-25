package com.jiucom.api.domain.user.repository;

import com.jiucom.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findBySocialTypeAndSocialId(com.jiucom.api.domain.user.entity.enums.SocialType socialType, String socialId);

    long countByCreatedAtAfter(LocalDateTime dateTime);
}
