package com.jobassistant.jabackend.app.company.mapper;

import com.jobassistant.jabackend.app.company.dto.request.CreateCompanyRequest;
import com.jobassistant.jabackend.app.company.dto.response.CompanyDetailResponse;
import com.jobassistant.jabackend.app.company.dto.response.CompanySummaryResponse;
import com.jobassistant.jabackend.app.company.entity.Company;
import com.jobassistant.jabackend.core.mapStruct.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface CompanyMapper {

    /**
     * Company 엔티티 → 목록 응답 DTO
     * boolean isDefault → MapStruct가 프로퍼티명을 "default"로 인식하므로 명시적 매핑 필요
     */
    @Mapping(source = "default", target = "isDefault")
    CompanySummaryResponse toSummary(Company company);

    /**
     * Company 엔티티 → 상세 응답 DTO
     * BaseEntity 필드명이 snake_case이므로 명시적 매핑 필요
     * boolean isDefault 동일하게 명시적 매핑 필요
     */
    @Mapping(source = "created_at", target = "createdAt")
    @Mapping(source = "updated_at", target = "updatedAt")
    @Mapping(source = "default", target = "isDefault")
    CompanyDetailResponse toDetail(Company company);

    /**
     * 생성 요청 DTO → Company 엔티티
     * user 필드는 서비스에서 별도로 설정 (매핑 제외)
     */
    @Mapping(target = "user", ignore = true)
    Company toEntity(CreateCompanyRequest request);
}
