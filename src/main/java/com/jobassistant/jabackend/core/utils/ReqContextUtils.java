package com.jobassistant.jabackend.core.utils;

import com.jobassistant.jabackend.core.base.vo.ReqContextVo;
import com.jobassistant.jabackend.core.constants.RequestAttributeKey;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReqContextUtils {

    public ReqContextVo getReqContext() {
        HttpServletRequest request = ServletUtils.getServletRequest();

        if (request == null) return null; // 요청 객체가 없으면 null 반환

        Object context = request.getAttribute(RequestAttributeKey.REQUEST_CONTEXT.name());

        if (context instanceof ReqContextVo) return (ReqContextVo) context;

        return null; // 컨텍스트가 없거나 타입이 다르면 null 반환
    }
}
