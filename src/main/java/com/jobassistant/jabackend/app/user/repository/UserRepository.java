package com.jobassistant.jabackend.app.user.repository;

import com.jobassistant.jabackend.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
