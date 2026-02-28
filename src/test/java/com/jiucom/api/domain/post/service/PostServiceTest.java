package com.jiucom.api.domain.post.service;

import com.jiucom.api.domain.post.dto.response.PostDetailResponse;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.RedisUtil;
import com.jiucom.api.global.util.TestSecurityContextHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisUtil redisUtil;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com").password("enc").nickname("tester")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(testUser, 1L);

        testPost = Post.builder()
                .author(testUser).boardType(BoardType.FREE).title("테스트 게시글").content("내용").build();
        setId(testPost, 1L);

        TestSecurityContextHelper.setAuthentication(1L);
    }

    @AfterEach
    void tearDown() {
        TestSecurityContextHelper.clearAuthentication();
    }

    private void setId(Object entity, Long id) {
        try {
            Field f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @DisplayName("게시글 목록 조회")
    void getPosts_success() {
        given(postRepository.findByIsDeletedFalse(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(testPost)));

        Page result = postService.getPosts(null, 0, 20);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 성공")
    void getPostDetail_success() {
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        PostDetailResponse response = postService.getPostDetail(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 게시글");
        assertThat(response.getBoardType()).isEqualTo("FREE");
    }

    @Test
    @DisplayName("게시글 상세 조회 - 존재하지 않음")
    void getPostDetail_notFound() {
        given(postRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostDetail(99L))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.POST_NOT_FOUND));
    }

    @Test
    @DisplayName("게시글 삭제 - 작성자 권한 검증")
    void deletePost_notAuthor() {
        TestSecurityContextHelper.setAuthentication(999L);
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        assertThatThrownBy(() -> postService.deletePost(1L))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.POST_NOT_AUTHOR));
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_success() {
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));

        postService.deletePost(1L);
        assertThat(testPost.isDeleted()).isTrue();
    }
}
