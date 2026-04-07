package com.jobassistant.jabackend.core.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 로컬 개발용 인증 필터 — JWT 구현 전까지 userId=1 고정 주입
 * local 프로파일에서만 활성화됩니다.
 */
@Component
@Profile("local")
public class DevAuthFilter extends OncePerRequestFilter {

    private static final Long DEV_USER_ID = 1L;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(DEV_USER_ID, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
