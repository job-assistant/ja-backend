package com.jobassistant.jabackend.core.jpa.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.jobassistant.jabackend")
@EnableJpaRepositories(basePackages = "com.jobassistant.jabackend")
public class JpaConfig {
}
