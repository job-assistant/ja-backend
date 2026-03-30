package com.jobassistant.jabackend.core.base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
//    @CreatedBy
//    @Comment("등록자_ID")
//    @Column(name = "REG_USER_ID", updatable = false, length = 50)
//    private String regUserId;

    @CreatedDate
    @Column(name = "created_at", updatable = false, columnDefinition = "등록_일시")
    private LocalDateTime created_at;

//    @LastModifiedBy
//    @Comment("수정자_ID")
//    @Column(name = "UPDATER_ID", length = 50)
//    private String updaterId;

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "수정_일시")
    private LocalDateTime updated_at;

}
