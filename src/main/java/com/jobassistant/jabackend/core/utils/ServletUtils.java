package com.jobassistant.jabackend.core.utils;

import com.jobassistant.jabackend.core.base.vo.ReqContextVo;
import com.jobassistant.jabackend.core.constants.RequestAttributeKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@UtilityClass
public class ServletUtils {
    public HttpServletRequest getServletRequest() {
        return Optional.of(RequestContextHolder.getRequestAttributes())
                .map(ra -> ((ServletRequestAttributes) ra).getRequest())
                .orElse(null);
    }

    public HttpServletResponse getServletResponse() {
        return Optional.of(RequestContextHolder.getRequestAttributes())
                .map(ra -> ((ServletRequestAttributes) ra).getResponse())
                .orElse(null);
    }

    public String getRequestURI() {
        return Optional.ofNullable(getServletRequest())
                .map(HttpServletRequest::getRequestURI)
                .orElse(null);
    }

    public String getHeader(String key) {
        return Optional.ofNullable(getServletRequest()).map(req -> req.getHeader(key))
                .orElse(null);
    }

    public ReqContextVo getServletRequestContextVo() {
        return (ReqContextVo) Optional.ofNullable(getServletRequest())
                .map(req -> req.getAttribute(RequestAttributeKey.REQUEST_CONTEXT.name()))
                .orElse(null);
    }

}
