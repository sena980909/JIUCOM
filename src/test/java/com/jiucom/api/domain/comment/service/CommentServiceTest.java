package com.jiucom.api.domain.comment.service;

import com.jiucom.api.domain.comment.dto.request.CommentCreateRequest;
import com.jiucom.api.domain.comment.dto.response.CommentResponse;
import com.jiucom.api.domain.comment.entity.Comment;
import com.jiucom.api.domain.comment.repository.CommentRepository;
import com.jiucom.api.domain.notification.service.NotificationService;
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

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private RedisUtil redisUtil;

    private User testUser;
    private User postAuthor;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("commenter@test.com").password("enc").nickname("댓글러")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(testUser, 2L);

        postAuthor = User.builder()
                .email("author@test.com").password("enc").nickname("작성자")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(postAuthor, 1L);

        testPost = Post.builder()
                .author(postAuthor).boardType(BoardType.FREE).title("게시글").content("내용").build();
        setId(testPost, 1L);

        TestSecurityContextHelper.setAuthentication(2L);
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
    @DisplayName("댓글 생성 - 성공 + 알림 발송")
    void createComment_success_sendsNotification() {
        given(userRepository.findById(2L)).willReturn(Optional.of(testUser));
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(commentRepository.save(any(Comment.class))).willAnswer(i -> i.getArgument(0));

        CommentCreateRequest request = new CommentCreateRequest();
        setField(request, "content", "좋은 글이네요!");

        CommentResponse response = commentService.createComment(1L, request);

        assertThat(response.getContent()).isEqualTo("좋은 글이네요!");
        assertThat(testPost.getCommentCount()).isEqualTo(1);
        verify(notificationService).sendNotification(eq(1L), any(), any(), any(), any());
    }

    @Test
    @DisplayName("댓글 생성 - 자기 게시글에는 알림 안 보냄")
    void createComment_selfPost_noNotification() {
        TestSecurityContextHelper.setAuthentication(1L); // same as post author
        given(userRepository.findById(1L)).willReturn(Optional.of(postAuthor));
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(commentRepository.save(any(Comment.class))).willAnswer(i -> i.getArgument(0));

        CommentCreateRequest request = new CommentCreateRequest();
        setField(request, "content", "자기 댓글");

        commentService.createComment(1L, request);

        // notificationService should NOT be called
        org.mockito.Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("댓글 삭제 - 작성자 권한 체크")
    void deleteComment_notAuthor() {
        TestSecurityContextHelper.setAuthentication(999L);
        Comment comment = Comment.builder()
                .post(testPost).author(testUser).content("댓글").build();
        setId(comment, 1L);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(1L))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.COMMENT_NOT_AUTHOR));
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
