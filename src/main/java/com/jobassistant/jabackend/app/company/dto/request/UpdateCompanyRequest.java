package com.jobassistant.jabackend.app.company.dto.request;

import com.jobassistant.jabackend.app.company.constant.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 회사 수정 요청 DTO
 */
public record UpdateCompanyRequest(

        @NotBlank(message = "회사명은 필수입니다.")
        @Size(max = 100, message = "회사명은 100자 이하여야 합니다.")
        String name,

        @Size(max = 100, message = "지원 직무는 100자 이하여야 합니다.")
        String position,

        @NotNull(message = "지원 상태는 필수입니다.")
        Status status,

        LocalDate interviewDate

) {}
