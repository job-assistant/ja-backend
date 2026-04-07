package com.jobassistant.jabackend.app.company.controller;

import com.jobassistant.jabackend.app.company.dto.request.CompanySearchCondition;
import com.jobassistant.jabackend.app.company.dto.request.CreateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.request.UpdateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.response.CompanyDetailResponse;
import com.jobassistant.jabackend.app.company.dto.response.CompanySummaryResponse;
import com.jobassistant.jabackend.app.company.service.CompanyService;
import com.jobassistant.jabackend.core.base.component.AbstractController;
import com.jobassistant.jabackend.core.base.vo.BizRespVo;
import com.jobassistant.jabackend.core.base.vo.GenericListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Company", description = "지원 회사 관리 API")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController extends AbstractController {

    private final CompanyService companyService;

    @Operation(summary = "회사 목록 조회", description = "이름 키워드, 지원 상태 조건으로 필터링한 목록을 반환합니다.")
    @GetMapping
    public BizRespVo<GenericListVo<CompanySummaryResponse>> getCompanies(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute CompanySearchCondition condition
    ) {
        return makeGenericListResponse(companyService.getCompanies(userId, condition));
    }

    @Operation(summary = "회사 단건 조회", description = "회사 ID로 상세 정보를 반환합니다.")
    @GetMapping("/{companyId}")
    public BizRespVo<CompanyDetailResponse> getCompany(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long companyId
    ) {
        return makeResponse(companyService.getCompany(companyId, userId));
    }

    @Operation(summary = "회사 등록", description = "새 지원 회사를 등록합니다.")
    @PostMapping
    public BizRespVo<CompanyDetailResponse> registerCompany(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid CreateCompanyRequest request
    ) {
        return makeCreatedResponse(companyService.registerCompany(userId, request));
    }

    @Operation(summary = "회사 수정", description = "회사 이름, 직무, 지원 상태, 면접 일정을 수정합니다.")
    @PutMapping("/{companyId}")
    public BizRespVo<CompanyDetailResponse> updateCompany(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long companyId,
            @RequestBody @Valid UpdateCompanyRequest request
    ) {
        return makeResponse(companyService.updateCompany(companyId, userId, request));
    }

    @Operation(summary = "기본 회사 설정", description = "해당 회사를 기본 회사로 설정합니다. 기존 기본 회사는 자동 해제됩니다.")
    @PatchMapping("/{companyId}/default")
    public BizRespVo<Void> updateDefault(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long companyId
    ) {
        companyService.updateDefault(companyId, userId);
        return makeResponse(null);
    }

    @Operation(summary = "회사 삭제", description = "회사를 삭제합니다.")
    @DeleteMapping("/{companyId}")
    public BizRespVo<Void> removeCompany(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long companyId
    ) {
        companyService.removeCompany(companyId, userId);
        return makeResponse(null);
    }
}
