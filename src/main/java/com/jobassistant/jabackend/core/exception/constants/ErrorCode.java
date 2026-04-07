package com.jobassistant.jabackend.core.exception.constants;

import lombok.Getter;


public enum ErrorCode {

    INTERNAL_SERVER_ERROR("ERROR_01"),

    // Company
    COMPANY_NOT_FOUND("COMPANY_01"),
    COMPANY_ACCESS_DENIED("COMPANY_02");

    @Getter
    private final String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
