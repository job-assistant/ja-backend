package com.jobassistant.jabackend.app.company.dto.request;

import com.jobassistant.jabackend.app.company.constant.Status;

/**
 * 회사 목록 조회 검색 조건 DTO
 * - name: 회사명 키워드 (부분 일치)
 * - status: 지원 상태 필터
 */
public record CompanySearchCondition(

        String name,

        Status status

) {}
