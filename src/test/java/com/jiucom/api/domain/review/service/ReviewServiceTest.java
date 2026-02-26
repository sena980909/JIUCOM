package com.jiucom.api.domain.review.service;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.review.dto.request.ReviewCreateRequest;
import com.jiucom.api.domain.review.dto.response.ReviewResponse;
import com.jiucom.api.domain.review.entity.Review;
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

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private PartRepository partRepository;
    @Mock
    private UserRepository userRepository;

    private User testUser;
    private Part testPart;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com").password("enc").nickname("tester")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(testUser, 1L);

        testPart = Part.builder()
                .name("RTX 4070").category(PartCategory.GPU).manufacturer("NVIDIA").build();
        setId(testPart, 1L);

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
    @DisplayName("리뷰 생성 - 성공")
    void createReview_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(partRepository.findById(1L)).willReturn(Optional.of(testPart));
        given(reviewRepository.existsByAuthorIdAndPartId(1L, 1L)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willAnswer(i -> i.getArgument(0));

        ReviewCreateRequest request = new ReviewCreateRequest();
        setField(request, "partId", 1L);
        setField(request, "rating", 5);
        setField(request, "content", "좋은 제품입니다!");

        ReviewResponse response = reviewService.createReview(request);

        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("좋은 제품입니다!");
    }

    @Test
    @DisplayName("리뷰 생성 - 중복 리뷰 거부")
    void createReview_duplicate() {
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(partRepository.findById(1L)).willReturn(Optional.of(testPart));
        given(reviewRepository.existsByAuthorIdAndPartId(1L, 1L)).willReturn(true);

        ReviewCreateRequest request = new ReviewCreateRequest();
        setField(request, "partId", 1L);
        setField(request, "rating", 4);
        setField(request, "content", "두번째 리뷰");

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.REVIEW_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("리뷰 삭제 - 작성자 권한 체크")
    void deleteReview_notAuthor() {
        TestSecurityContextHelper.setAuthentication(999L);
        Review review = Review.builder()
                .author(testUser).part(testPart).rating(5).content("리뷰").build();
        setId(review, 1L);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(1L))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.REVIEW_NOT_AUTHOR));
    }

    @Test
    @DisplayName("리뷰 삭제 - 성공")
    void deleteReview_success() {
        Review review = Review.builder()
                .author(testUser).part(testPart).rating(5).content("리뷰").build();
        setId(review, 1L);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        reviewService.deleteReview(1L);
        assertThat(review.isDeleted()).isTrue();
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
