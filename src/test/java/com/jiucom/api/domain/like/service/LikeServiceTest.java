package com.jiucom.api.domain.like.service;

import com.jiucom.api.domain.build.repository.BuildRepository;
import com.jiucom.api.domain.comment.repository.CommentRepository;
import com.jiucom.api.domain.like.dto.response.LikeResponse;
import com.jiucom.api.domain.like.entity.ContentLike;
import com.jiucom.api.domain.like.entity.enums.LikeTargetType;
import com.jiucom.api.domain.like.repository.ContentLikeRepository;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.review.repository.ReviewRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.TestSecurityContextHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private ContentLikeRepository contentLikeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BuildRepository buildRepository;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com").password("enc").nickname("tester")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(testUser, 1L);

        testPost = Post.builder()
                .author(testUser).boardType(BoardType.FREE).title("게시글").content("내용").build();
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
    @DisplayName("좋아요 토글 - 추가")
    void toggleLike_add() {
        given(postRepository.existsById(1L)).willReturn(true);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(contentLikeRepository.findByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.POST, 1L))
                .willReturn(Optional.empty());
        given(contentLikeRepository.save(any(ContentLike.class))).willAnswer(i -> i.getArgument(0));
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(contentLikeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, 1L)).willReturn(1L);

        LikeResponse response = likeService.toggleLike(LikeTargetType.POST, 1L);

        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(1L);
        assertThat(testPost.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 토글 - 취소")
    void toggleLike_remove() {
        ContentLike existing = ContentLike.builder()
                .user(testUser).targetType(LikeTargetType.POST).targetId(1L).build();
        setId(existing, 1L);

        given(postRepository.existsById(1L)).willReturn(true);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(contentLikeRepository.findByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.POST, 1L))
                .willReturn(Optional.of(existing));
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(contentLikeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, 1L)).willReturn(0L);

        LikeResponse response = likeService.toggleLike(LikeTargetType.POST, 1L);

        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(0L);
        verify(contentLikeRepository).delete(existing);
    }

    @Test
    @DisplayName("좋아요 토글 - 대상 없음")
    void toggleLike_targetNotFound() {
        given(postRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> likeService.toggleLike(LikeTargetType.POST, 99L))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.LIKE_TARGET_NOT_FOUND));
    }

    @Test
    @DisplayName("좋아요 상태 조회 - 로그인 유저")
    void getLikeStatus_authenticated() {
        given(postRepository.existsById(1L)).willReturn(true);
        given(contentLikeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, 1L)).willReturn(3L);
        given(contentLikeRepository.existsByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.POST, 1L))
                .willReturn(true);

        LikeResponse response = likeService.getLikeStatus(LikeTargetType.POST, 1L);

        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("좋아요 상태 조회 - 비로그인 유저")
    void getLikeStatus_anonymous() {
        TestSecurityContextHelper.clearAuthentication();

        given(postRepository.existsById(1L)).willReturn(true);
        given(contentLikeRepository.countByTargetTypeAndTargetId(LikeTargetType.POST, 1L)).willReturn(3L);

        LikeResponse response = likeService.getLikeStatus(LikeTargetType.POST, 1L);

        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(3L);
    }
}
