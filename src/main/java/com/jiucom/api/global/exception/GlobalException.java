package com.jiucom.api.global.exception;

import com.jiucom.api.global.exception.code.GlobalErrorCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final GlobalErrorCode errorCode;

    public GlobalException(GlobalErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
