package com.jobassistant.jabackend.app.company.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    applied,
    document,
    first,
    second,
    final_,
    passed,
    failed;

    /**
     * JSON 역직렬화 시 대소문자 무관하게 처리, "final" → final_ 변환
     */
    @JsonCreator
    public static Status fromString(String value) {
        if (value == null) return null;
        String lower = value.toLowerCase();
        if ("final".equals(lower)) return final_;
        return Status.valueOf(lower);
    }
}