package com.jobassistant.jabackend.core.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {

    /**
     * 현재 인증된 Authentication 객체를 반환합니다.
     * 인증되지 않았거나 anonymousUser인 경우 AccessDeniedException 발생.
     */
    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }

        return authentication;
    }

}
