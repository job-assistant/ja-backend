package com.jobassistant.jabackend.app.company.service;

import com.jobassistant.jabackend.app.company.dto.request.CompanySearchCondition;
import com.jobassistant.jabackend.app.company.dto.request.CreateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.request.UpdateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.response.CompanyDetailResponse;
import com.jobassistant.jabackend.app.company.dto.response.CompanySummaryResponse;

import java.util.List;

public interface CompanyService {

    /**
     * 회사 단건 조회 — 존재하지 않거나 접근 권한 없으면 BizException 발생
     */
    CompanyDetailResponse getCompany(Long companyId, Long userId);

    /**
     * 회사 목록 조회 — 검색 조건(이름, 상태)에 따라 필터링
     */
    List<CompanySummaryResponse> getCompanies(Long userId, CompanySearchCondition condition);

    /**
     * 회사 등록
     */
    CompanyDetailResponse registerCompany(Long userId, CreateCompanyRequest request);

    /**
     * 회사 정보 수정 — 이름, 직무, 상태, 면접일 변경
     */
    CompanyDetailResponse updateCompany(Long companyId, Long userId, UpdateCompanyRequest request);

    /**
     * 기본 회사 설정 변경 — 기존 기본 회사를 해제하고 해당 회사를 기본으로 설정
     */
    void updateDefault(Long companyId, Long userId);

    /**
     * 회사 삭제 — 접근 권한 없으면 BizException 발생
     */
    void removeCompany(Long companyId, Long userId);
}
