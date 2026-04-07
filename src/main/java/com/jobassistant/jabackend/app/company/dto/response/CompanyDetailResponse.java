package com.jobassistant.jabackend.app.company.dto.response;

import com.jobassistant.jabackend.app.company.constant.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회사 상세 응답 DTO (전체 필드)
 */
public record CompanyDetailResponse(

        Long id,
        String name,
        String position,
        Status status,
        LocalDate interviewDate,
        boolean isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}
