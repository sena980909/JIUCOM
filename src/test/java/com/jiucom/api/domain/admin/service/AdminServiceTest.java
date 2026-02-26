package com.jiucom.api.domain.admin.service;

import com.jiucom.api.domain.admin.dto.response.DashboardResponse;
import com.jiucom.api.domain.build.repository.BuildRepository;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BuildRepository buildRepository;
    @Mock
    private PartRepository partRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("대시보드 조회 - 통계 반환")
    void getDashboard_success() {
        given(userRepository.count()).willReturn(100L);
        given(buildRepository.count()).willReturn(50L);
        given(partRepository.count()).willReturn(200L);
        given(postRepository.countByIsDeletedFalse()).willReturn(30L);
        given(userRepository.countByCreatedAtAfter(any())).willReturn(5L);

        User recentUser = User.builder()
                .email("r@test.com").password("enc").nickname("recent")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(recentUser, 1L);
        setField(recentUser, "createdAt", LocalDateTime.now());
        given(userRepository.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(recentUser)));

        DashboardResponse response = adminService.getDashboard();

        assertThat(response.getTotalUsers()).isEqualTo(100L);
        assertThat(response.getTotalBuilds()).isEqualTo(50L);
        assertThat(response.getTotalParts()).isEqualTo(200L);
        assertThat(response.getTotalPosts()).isEqualTo(30L);
        assertThat(response.getTodaySignups()).isEqualTo(5L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void setField(Object entity, String fieldName, Object value) {
        try {
            Field f = entity.getClass().getSuperclass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(entity, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
