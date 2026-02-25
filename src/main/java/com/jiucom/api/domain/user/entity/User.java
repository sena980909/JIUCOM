package com.jiucom.api.domain.user.entity;

import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SocialType socialType;

    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UserStatus status;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
