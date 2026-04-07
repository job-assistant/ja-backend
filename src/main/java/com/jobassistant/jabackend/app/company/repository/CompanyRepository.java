package com.jobassistant.jabackend.app.company.repository;

import com.jobassistant.jabackend.app.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {

    /** 특정 사용자의 단건 조회 */
    Optional<Company> findByIdAndUserId(Long id, Long userId);

    /** 특정 사용자의 전체 목록 조회 (updateDefault에서 사용) */
    List<Company> findAllByUserId(Long userId);

    /** ID + 사용자 ID 기준 존재 여부 확인 */
    boolean existsByIdAndUserId(Long id, Long userId);

    /** ID + 사용자 ID 기준 삭제 */
    void deleteByIdAndUserId(Long id, Long userId);
}
