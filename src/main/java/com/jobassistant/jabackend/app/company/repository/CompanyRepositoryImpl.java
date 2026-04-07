package com.jobassistant.jabackend.app.company.repository;

import com.jobassistant.jabackend.app.company.dto.request.CompanySearchCondition;
import com.jobassistant.jabackend.app.company.entity.Company;
import com.jobassistant.jabackend.app.company.entity.QCompany;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QCompany company = QCompany.company;

    @Override
    public List<Company> searchByCondition(Long userId, CompanySearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        // 필수 조건: 본인 소유 회사만 조회
        builder.and(company.user.id.eq(userId));

        // 선택 조건: 이름 키워드 (부분 일치)
        if (StringUtils.hasText(condition.name())) {
            builder.and(company.name.containsIgnoreCase(condition.name()));
        }

        // 선택 조건: 지원 상태 필터
        if (condition.status() != null) {
            builder.and(company.status.eq(condition.status()));
        }

        return queryFactory
                .selectFrom(company)
                .where(builder)
                .orderBy(company.created_at.desc())
                .fetch();
    }
}
