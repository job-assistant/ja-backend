package com.jobassistant.jabackend.core.exception.constants;

import lombok.Getter;


public enum ErrorCode {

    INTERNAL_SERVER_ERROR("ERROR_01"),

    // Company
    COMPANY_NOT_FOUND("COMPANY_01"),
    COMPANY_ACCESS_DENIED("COMPANY_02"),

    // File
    FILE_EMPTY("FILE_01"),
    FILE_STORE_FAILED("FILE_02"),
    FILE_DELETE_FAILED("FILE_03"),
    FILE_INVALID_PATH("FILE_04"),
    FILE_STORAGE_INIT_FAILED("FILE_05");

    @Getter
    private final String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
