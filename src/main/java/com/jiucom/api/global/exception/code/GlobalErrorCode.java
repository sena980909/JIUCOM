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

    // Rate Limit
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "JIUCOM-429", "요청 횟수가 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
