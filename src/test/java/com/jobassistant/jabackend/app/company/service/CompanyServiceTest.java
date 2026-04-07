package com.jobassistant.jabackend.app.company.service;

import com.jobassistant.jabackend.app.company.constant.Status;
import com.jobassistant.jabackend.app.company.dto.request.CompanySearchCondition;
import com.jobassistant.jabackend.app.company.dto.request.CreateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.request.UpdateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.response.CompanyDetailResponse;
import com.jobassistant.jabackend.app.company.dto.response.CompanySummaryResponse;
import com.jobassistant.jabackend.app.company.entity.Company;
import com.jobassistant.jabackend.app.company.mapper.CompanyMapper;
import com.jobassistant.jabackend.app.company.repository.CompanyRepository;
import com.jobassistant.jabackend.app.company.service.impl.CompanyServiceImpl;
import com.jobassistant.jabackend.app.user.entity.User;
import com.jobassistant.jabackend.app.user.repository.UserRepository;
import com.jobassistant.jabackend.core.base.exception.BizException;
import com.jobassistant.jabackend.core.exception.constants.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private UserRepository userRepository;

    private static final Long USER_ID = 1L;
    private static final Long COMPANY_ID = 10L;

    private User user;
    private Company company;
    private Company defaultCompany;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .name("테스터")
                .provider("google")
                .providerId("google-id-123")
                .build();
        ReflectionTestUtils.setField(user, "id", USER_ID);

        company = Company.builder()
                .user(user)
                .name("카카오")
                .position("백엔드 개발자")
                .status(Status.applied)
                .interviewDate(LocalDate.of(2026, 5, 1))
                .isDefault(false)
                .build();
        ReflectionTestUtils.setField(company, "id", COMPANY_ID);

        defaultCompany = Company.builder()
                .user(user)
                .name("네이버")
                .position("서버 개발자")
                .status(Status.first)
                .interviewDate(null)
                .isDefault(true)
                .build();
        ReflectionTestUtils.setField(defaultCompany, "id", 20L);
    }

    // ── getCompany ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getCompany: 회사 단건 조회")
    class GetCompany {

        @Test
        @DisplayName("성공 — 존재하는 회사 조회 시 상세 응답을 반환한다")
        void success() {
            // given
            CompanyDetailResponse expectedResponse = new CompanyDetailResponse(
                    COMPANY_ID, "카카오", "백엔드 개발자", Status.applied,
                    LocalDate.of(2026, 5, 1), false, LocalDateTime.now(), LocalDateTime.now()
            );
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.of(company));
            given(companyMapper.toDetail(company)).willReturn(expectedResponse);

            // when
            CompanyDetailResponse result = companyService.getCompany(COMPANY_ID, USER_ID);

            // then
            assertThat(result.id()).isEqualTo(COMPANY_ID);
            assertThat(result.name()).isEqualTo("카카오");
            assertThat(result.status()).isEqualTo(Status.applied);
        }

        @Test
        @DisplayName("실패 — 존재하지 않는 회사 조회 시 BizException(404)을 던진다")
        void fail_notFound() {
            // given
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyService.getCompany(COMPANY_ID, USER_ID))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> {
                        BizException ex = (BizException) e;
                        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.getErrorCode());
                    });
        }
    }

    // ── getCompanies ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getCompanies: 회사 목록 조회")
    class GetCompanies {

        @Test
        @DisplayName("성공 — 검색 조건에 맞는 회사 목록을 반환한다")
        void success() {
            // given
            CompanySearchCondition condition = new CompanySearchCondition("카카오", Status.applied);
            CompanySummaryResponse summaryResponse = new CompanySummaryResponse(
                    COMPANY_ID, "카카오", "백엔드 개발자", Status.applied, false
            );
            given(companyRepository.searchByCondition(USER_ID, condition)).willReturn(List.of(company));
            given(companyMapper.toSummary(company)).willReturn(summaryResponse);

            // when
            List<CompanySummaryResponse> result = companyService.getCompanies(USER_ID, condition);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("카카오");
        }

        @Test
        @DisplayName("성공 — 검색 결과가 없으면 빈 리스트를 반환한다")
        void success_emptyResult() {
            // given
            CompanySearchCondition condition = new CompanySearchCondition("없는회사", null);
            given(companyRepository.searchByCondition(USER_ID, condition)).willReturn(List.of());

            // when
            List<CompanySummaryResponse> result = companyService.getCompanies(USER_ID, condition);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ── registerCompany ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("registerCompany: 회사 등록")
    class RegisterCompany {

        @Test
        @DisplayName("성공 — 요청 정보로 회사를 저장하고 상세 응답을 반환한다")
        void success() {
            // given
            CreateCompanyRequest request = new CreateCompanyRequest(
                    "라인", "iOS 개발자", Status.applied, null, false
            );
            Company saved = Company.builder()
                    .user(user)
                    .name("라인")
                    .position("iOS 개발자")
                    .status(Status.applied)
                    .interviewDate(null)
                    .isDefault(false)
                    .build();
            CompanyDetailResponse expectedResponse = new CompanyDetailResponse(
                    2L, "라인", "iOS 개발자", Status.applied, null, false,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            given(userRepository.getReferenceById(USER_ID)).willReturn(user);
            given(companyRepository.save(any(Company.class))).willReturn(saved);
            given(companyMapper.toDetail(saved)).willReturn(expectedResponse);

            // when
            CompanyDetailResponse result = companyService.registerCompany(USER_ID, request);

            // then
            assertThat(result.name()).isEqualTo("라인");
            assertThat(result.status()).isEqualTo(Status.applied);
            then(companyRepository).should().save(any(Company.class));
        }
    }

    // ── updateCompany ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateCompany: 회사 수정")
    class UpdateCompany {

        @Test
        @DisplayName("성공 — 회사 정보를 수정하고 갱신된 상세 응답을 반환한다")
        void success() {
            // given
            UpdateCompanyRequest request = new UpdateCompanyRequest(
                    "카카오페이", "프론트엔드 개발자", Status.document, LocalDate.of(2026, 6, 15)
            );
            CompanyDetailResponse expectedResponse = new CompanyDetailResponse(
                    COMPANY_ID, "카카오페이", "프론트엔드 개발자", Status.document,
                    LocalDate.of(2026, 6, 15), false, LocalDateTime.now(), LocalDateTime.now()
            );
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.of(company));
            given(companyMapper.toDetail(company)).willReturn(expectedResponse);

            // when
            CompanyDetailResponse result = companyService.updateCompany(COMPANY_ID, USER_ID, request);

            // then
            assertThat(result.name()).isEqualTo("카카오페이");
            assertThat(result.status()).isEqualTo(Status.document);
            assertThat(result.interviewDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        }

        @Test
        @DisplayName("실패 — 존재하지 않는 회사 수정 시 BizException(404)을 던진다")
        void fail_notFound() {
            // given
            UpdateCompanyRequest request = new UpdateCompanyRequest(
                    "카카오페이", "프론트엔드 개발자", Status.document, null
            );
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyService.updateCompany(COMPANY_ID, USER_ID, request))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> {
                        BizException ex = (BizException) e;
                        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.getErrorCode());
                    });
        }
    }

    // ── updateDefault ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateDefault: 기본 회사 설정")
    class UpdateDefault {

        @Test
        @DisplayName("성공 — 기존 기본 회사가 해제되고 지정 회사가 기본으로 설정된다")
        void success_prevDefaultUnset() {
            // given
            given(companyRepository.findAllByUserId(USER_ID)).willReturn(List.of(defaultCompany, company));
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.of(company));

            // when
            companyService.updateDefault(COMPANY_ID, USER_ID);

            // then
            assertThat(defaultCompany.isDefault()).isFalse();
            assertThat(company.isDefault()).isTrue();
        }

        @Test
        @DisplayName("성공 — 기존 기본 회사가 없어도 지정 회사가 기본으로 설정된다")
        void success_noPrevDefault() {
            // given
            given(companyRepository.findAllByUserId(USER_ID)).willReturn(List.of(company));
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.of(company));

            // when
            companyService.updateDefault(COMPANY_ID, USER_ID);

            // then
            assertThat(company.isDefault()).isTrue();
        }

        @Test
        @DisplayName("실패 — 존재하지 않는 회사를 기본으로 설정 시 BizException(404)을 던진다")
        void fail_notFound() {
            // given
            given(companyRepository.findAllByUserId(USER_ID)).willReturn(List.of());
            given(companyRepository.findByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> companyService.updateDefault(COMPANY_ID, USER_ID))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> {
                        BizException ex = (BizException) e;
                        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.getErrorCode());
                    });
        }
    }

    // ── removeCompany ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("removeCompany: 회사 삭제")
    class RemoveCompany {

        @Test
        @DisplayName("성공 — 회사가 존재하면 deleteByIdAndUserId를 호출한다")
        void success() {
            // given
            given(companyRepository.existsByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(true);

            // when
            companyService.removeCompany(COMPANY_ID, USER_ID);

            // then
            then(companyRepository).should().deleteByIdAndUserId(COMPANY_ID, USER_ID);
        }

        @Test
        @DisplayName("실패 — 존재하지 않는 회사 삭제 시 BizException(404)을 던지고 delete를 호출하지 않는다")
        void fail_notFound() {
            // given
            given(companyRepository.existsByIdAndUserId(COMPANY_ID, USER_ID)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> companyService.removeCompany(COMPANY_ID, USER_ID))
                    .isInstanceOf(BizException.class)
                    .satisfies(e -> {
                        BizException ex = (BizException) e;
                        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.COMPANY_NOT_FOUND.getErrorCode());
                    });

            then(companyRepository).should(never()).deleteByIdAndUserId(any(), any());
        }
    }
}
