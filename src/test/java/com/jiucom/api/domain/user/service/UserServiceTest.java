package com.jiucom.api.domain.user.service;

import com.jiucom.api.domain.user.dto.request.UpdateProfileRequest;
import com.jiucom.api.domain.user.dto.response.UserProfileResponse;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("testuser")
                .role(Role.USER)
                .socialType(SocialType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();
        setId(testUser, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UpdateProfileRequest createUpdateRequest(String nickname, String imageUrl) {
        try {
            UpdateProfileRequest request = UpdateProfileRequest.class.getDeclaredConstructor().newInstance();
            if (nickname != null) {
                Field f = UpdateProfileRequest.class.getDeclaredField("nickname");
                f.setAccessible(true);
                f.set(request, nickname);
            }
            if (imageUrl != null) {
                Field f = UpdateProfileRequest.class.getDeclaredField("profileImageUrl");
                f.setAccessible(true);
                f.set(request, imageUrl);
            }
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("프로필 조회")
    class GetProfile {

        @Test
        @DisplayName("성공 - 프로필 조회")
        void getProfile_success() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            UserProfileResponse response = userService.getProfile(1L);

            assertThat(response.getEmail()).isEqualTo("test@test.com");
            assertThat(response.getNickname()).isEqualTo("testuser");
            assertThat(response.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void getProfile_userNotFound() {
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getProfile(1L))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.USER_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("프로필 수정")
    class UpdateProfile {

        @Test
        @DisplayName("성공 - 닉네임 변경")
        void updateProfile_nicknameChange() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userRepository.existsByNickname("newnick")).willReturn(false);

            UserProfileResponse response = userService.updateProfile(1L,
                    createUpdateRequest("newnick", null));

            assertThat(response.getNickname()).isEqualTo("newnick");
        }

        @Test
        @DisplayName("실패 - 중복 닉네임")
        void updateProfile_duplicateNickname() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(userRepository.existsByNickname("taken")).willReturn(true);

            assertThatThrownBy(() -> userService.updateProfile(1L,
                    createUpdateRequest("taken", null)))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.DUPLICATE_NICKNAME));
        }

        @Test
        @DisplayName("성공 - 프로필 이미지 변경")
        void updateProfile_imageChange() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            UserProfileResponse response = userService.updateProfile(1L,
                    createUpdateRequest(null, "https://img.com/new.jpg"));

            assertThat(response.getProfileImageUrl()).isEqualTo("https://img.com/new.jpg");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void updateProfile_userNotFound() {
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateProfile(1L,
                    createUpdateRequest("newnick", null)))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.USER_NOT_FOUND));
        }
    }
}
