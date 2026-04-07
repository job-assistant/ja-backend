package com.jobassistant.jabackend.app.user.entity;

import com.jobassistant.jabackend.core.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_provider", columnNames = {"provider", "provider_id"})
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_provider", columnList = "provider, provider_id")
        }
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String picture;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Builder
    public User(String email, String name, String picture, String provider, String providerId) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void update(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
}
