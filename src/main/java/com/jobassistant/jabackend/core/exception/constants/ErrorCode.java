package com.jobassistant.jabackend.core.exception.constants;

import lombok.Getter;


public enum ErrorCode {

    INTERNAL_SERVER_ERROR("ERROR_01");

    @Getter
    private final String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
