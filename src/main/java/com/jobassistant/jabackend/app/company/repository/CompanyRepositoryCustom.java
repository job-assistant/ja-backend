package com.jobassistant.jabackend.app.company.repository;

import com.jobassistant.jabackend.app.company.dto.request.CompanySearchCondition;
import com.jobassistant.jabackend.app.company.entity.Company;

import java.util.List;

public interface CompanyRepositoryCustom {

    /** 동적 조건(이름, 상태)으로 사용자의 회사 목록 검색 */
    List<Company> searchByCondition(Long userId, CompanySearchCondition condition);
}
