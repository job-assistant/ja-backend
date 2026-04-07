package com.jobassistant.jabackend.app.company.service.impl;

import com.jobassistant.jabackend.app.company.dto.request.CompanySearchCondition;
import com.jobassistant.jabackend.app.company.dto.request.CreateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.request.UpdateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.response.CompanyDetailResponse;
import com.jobassistant.jabackend.app.company.dto.response.CompanySummaryResponse;
import com.jobassistant.jabackend.app.company.entity.Company;
import com.jobassistant.jabackend.app.company.mapper.CompanyMapper;
import com.jobassistant.jabackend.app.company.repository.CompanyRepository;
import com.jobassistant.jabackend.app.company.service.CompanyService;
import com.jobassistant.jabackend.app.user.entity.User;
import com.jobassistant.jabackend.app.user.repository.UserRepository;
import com.jobassistant.jabackend.core.base.component.AbstractService;
import com.jobassistant.jabackend.core.base.exception.BizException;
import com.jobassistant.jabackend.core.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl extends AbstractService implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserRepository userRepository;

    /**
     * 회사 단건 조회 — 존재하지 않거나 접근 권한 없으면 COMPANY_NOT_FOUND 예외 발생
     */
    @Override
    public CompanyDetailResponse getCompany(Long companyId, Long userId) {
        Company company = findCompanyOrThrow(companyId, userId);
        return companyMapper.toDetail(company);
    }

    /**
     * 회사 목록 조회 — QueryDSL BooleanBuilder로 동적 조건(이름, 상태) 필터링
     */
    @Override
    public List<CompanySummaryResponse> getCompanies(Long userId, CompanySearchCondition condition) {
        return companyRepository.searchByCondition(userId, condition)
                .stream()
                .map(companyMapper::toSummary)
                .toList();
    }

    /**
     * 회사 등록 — User 프록시 참조로 저장
     */
    @Override
    @Transactional
    public CompanyDetailResponse registerCompany(Long userId, CreateCompanyRequest request) {
        // getReferenceById: select 없이 프록시만 반환 → 불필요한 쿼리 방지
        User userRef = userRepository.getReferenceById(userId);
        // user 필드는 MapStruct 매핑 대상 제외 → Builder로 직접 설정
        Company saved = companyRepository.save(
                Company.builder()
                        .user(userRef)
                        .name(request.name())
                        .position(request.position())
                        .status(request.status())
                        .interviewDate(request.interviewDate())
                        .isDefault(request.isDefault())
                        .build()
        );
        return companyMapper.toDetail(saved);
    }

    /**
     * 회사 정보 수정 — 이름, 직무, 상태, 면접일 변경
     */
    @Override
    @Transactional
    public CompanyDetailResponse updateCompany(Long companyId, Long userId, UpdateCompanyRequest request) {
        Company company = findCompanyOrThrow(companyId, userId);
        company.update(request.name(), request.position(), request.status(), request.interviewDate());
        return companyMapper.toDetail(company);
    }

    /**
     * 기본 회사 설정 변경 — 기존 기본 회사 해제 후 지정 회사를 기본으로 설정
     */
    @Override
    @Transactional
    public void updateDefault(Long companyId, Long userId) {
        // 기존 기본 회사 해제
        companyRepository.findAllByUserId(userId).stream()
                .filter(Company::isDefault)
                .forEach(c -> c.updateDefault(false));

        Company company = findCompanyOrThrow(companyId, userId);
        company.updateDefault(true);
    }

    /**
     * 회사 삭제 — 접근 권한 없으면 COMPANY_NOT_FOUND 예외 발생
     */
    @Override
    @Transactional
    public void removeCompany(Long companyId, Long userId) {
        if (!companyRepository.existsByIdAndUserId(companyId, userId)) {
            throw new BizException(HttpStatus.NOT_FOUND, ErrorCode.COMPANY_NOT_FOUND.getErrorCode());
        }
        companyRepository.deleteByIdAndUserId(companyId, userId);
    }

    // ── private ──────────────────────────────────────────────────────────────

    /** ID + userId 기준으로 조회, 없으면 NOT_FOUND 예외 */
    private Company findCompanyOrThrow(Long companyId, Long userId) {
        return companyRepository.findByIdAndUserId(companyId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.COMPANY_NOT_FOUND.getErrorCode(),
                        "회사를 찾을 수 없습니다. id=" + companyId
                ));
    }
}
