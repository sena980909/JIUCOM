package com.jiucom.api.global.exception;

import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.exception.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(GlobalException e) {
        log.warn("GlobalException: {}", e.getMessage());
        GlobalErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponseDto.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("입력값 검증에 실패했습니다.");
        log.warn("Validation failed: {}", message);
        return ResponseEntity
                .status(GlobalErrorCode.INVALID_INPUT.getHttpStatus())
                .body(ErrorResponseDto.of(GlobalErrorCode.INVALID_INPUT, message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(GlobalErrorCode.METHOD_NOT_ALLOWED.getHttpStatus())
                .body(ErrorResponseDto.of(GlobalErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponseDto.of(GlobalErrorCode.INTERNAL_SERVER_ERROR));
    }
}
