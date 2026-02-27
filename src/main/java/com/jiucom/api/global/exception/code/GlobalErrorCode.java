package com.jiucom.api.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JIUCOM-500", "서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "JIUCOM-400", "잘못된 요청입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "JIUCOM-401", "입력값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-404", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "JIUCOM-405", "허용되지 않는 HTTP 메서드입니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "JIUCOM-A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "JIUCOM-A002", "접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JIUCOM-A003", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JIUCOM-A004", "유효하지 않은 토큰입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "JIUCOM-A005", "비밀번호가 일치하지 않습니다."),
    ALREADY_LOGGED_OUT(HttpStatus.BAD_REQUEST, "JIUCOM-A006", "이미 로그아웃된 상태입니다."),
    OAUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "JIUCOM-A007", "소셜 로그인에 실패했습니다."),
    OAUTH_INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "JIUCOM-A008", "지원하지 않는 소셜 로그인 제공자입니다."),
    OAUTH_EMAIL_DUPLICATE(HttpStatus.CONFLICT, "JIUCOM-A009", "이미 다른 방식으로 가입된 이메일입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "JIUCOM-U002", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "JIUCOM-U003", "이미 사용 중인 닉네임입니다."),

    // Part
    PART_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-P001", "부품을 찾을 수 없습니다."),
    INCOMPATIBLE_PARTS(HttpStatus.BAD_REQUEST, "JIUCOM-P002", "호환되지 않는 부품 조합입니다."),

    // Build
    BUILD_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-B001", "견적을 찾을 수 없습니다."),

    // Price
    PRICE_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-PR001", "가격 정보를 찾을 수 없습니다."),

    // Payment
    PAYMENT_NOT_READY(HttpStatus.SERVICE_UNAVAILABLE, "JIUCOM-PAY001", "구매나 결제는 아직 준비가 안되었습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-PAY002", "결제 정보를 찾을 수 없습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-PO001", "게시글을 찾을 수 없습니다."),
    POST_NOT_AUTHOR(HttpStatus.FORBIDDEN, "JIUCOM-PO002", "게시글 작성자만 수정/삭제할 수 있습니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-C001", "댓글을 찾을 수 없습니다."),
    COMMENT_NOT_AUTHOR(HttpStatus.FORBIDDEN, "JIUCOM-C002", "댓글 작성자만 수정/삭제할 수 있습니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-R001", "리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_AUTHOR(HttpStatus.FORBIDDEN, "JIUCOM-R002", "리뷰 작성자만 수정/삭제할 수 있습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "JIUCOM-R003", "이미 해당 부품에 리뷰를 작성했습니다."),

    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-N001", "알림을 찾을 수 없습니다."),

    // Like
    LIKE_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "JIUCOM-L001", "좋아요 대상을 찾을 수 없습니다."),

    // Image
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JIUCOM-IMG001", "이미지 업로드에 실패했습니다."),
    IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "JIUCOM-IMG002", "이미지 파일이 비어있습니다."),
    IMAGE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "JIUCOM-IMG003", "허용되지 않는 이미지 형식입니다. (jpeg, png, webp만 가능)"),

    // Rate Limit
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "JIUCOM-429", "요청 횟수가 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
