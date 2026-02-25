package com.jiucom.api.global.exception.dto;

import com.jiucom.api.global.exception.code.GlobalErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponseDto {

    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public static ErrorResponseDto of(GlobalErrorCode errorCode) {
        return ErrorResponseDto.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponseDto of(GlobalErrorCode errorCode, String message) {
        return ErrorResponseDto.builder()
                .code(errorCode.getCode())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
