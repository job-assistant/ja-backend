package com.jobassistant.jabackend.app.company.entity;

import com.jobassistant.jabackend.app.company.constant.Status;
import com.jobassistant.jabackend.app.user.entity.User;
import com.jobassistant.jabackend.core.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "companies",
        indexes = {
                @Index(name = "idx_companies_user_id", columnList = "user_id")
        }
)
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String position;

    @Column(nullable = false, length = 20)
    private Status status;

    @Column(name = "interview_date")
    private LocalDate interviewDate;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Builder
    public Company(User user, String name, String position, Status status, LocalDate interviewDate, boolean isDefault) {
        this.user = user;
        this.name = name;
        this.position = position;
        this.status = status != null ? status : Status.applied;
        this.interviewDate = interviewDate;
        this.isDefault = isDefault;
    }

    public void update(String name, String position, Status status, LocalDate interviewDate) {
        this.name = name;
        this.position = position;
        this.status = status;
        this.interviewDate = interviewDate;
    }

    public void updateDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
