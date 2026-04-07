package com.jobassistant.jabackend.app.company.dto.response;

import com.jobassistant.jabackend.app.company.constant.Status;

/**
 * 회사 목록 응답 DTO (최소 필드)
 */
public record CompanySummaryResponse(

        Long id,
        String name,
        String position,
        Status status,
        boolean isDefault

) {}
