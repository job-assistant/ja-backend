package com.jobassistant.jabackend.core.base.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BizException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BizException(HttpStatus status, String errorCode) {
        super("");
        this.status = status;
        this.errorCode = errorCode;
    }

    public BizException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}