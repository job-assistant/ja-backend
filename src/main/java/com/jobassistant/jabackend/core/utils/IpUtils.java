package com.jobassistant.jabackend.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class IpUtils {
    public String getClientIp(HttpServletRequest request) {
        // Check headers set by proxies or load balancers
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For can contain a list of IPs. The first one is the client's IP.
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 기본적으로 remoteAddr 사용
        ip = request.getRemoteAddr();

        // IPv6 로컬호스트 처리
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        // Fall back to the remote address
        return ip;
    }
}
