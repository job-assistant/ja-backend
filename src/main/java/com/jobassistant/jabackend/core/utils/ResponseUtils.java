package com.jobassistant.jabackend.core.utils;


import com.jobassistant.jabackend.core.base.vo.BizErrorVo;
import com.jobassistant.jabackend.core.base.vo.BizRespVo;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

@UtilityClass
public class ResponseUtils {
    public <T> BizRespVo<T> makeResponse(HttpStatus httpStatus, final T body) {


        return makeResponse(httpStatus, body);
    }

    public <T> BizRespVo<T> makeResponse(HttpStatus httpStatus, String message, final T body) {


        return BizRespVo.withStatus(httpStatus, message, body);
    }

    public BizRespVo<BizErrorVo> makeErrorResponse(HttpStatus httpStatus,
                                                   String errorCode,
                                                   String message,
                                                   Throwable th,
                                                   boolean debug) {

        String path = ServletUtils.getRequestURI();

        BizErrorVo bizErrorVo = makeErrorVo(errorCode, path, th, debug);

        return makeResponse(httpStatus, message, bizErrorVo);
    }

    public BizErrorVo makeErrorVo(String errorCode,
                                  String path,
                                  Throwable th,
                                  boolean includeStackTrace) {

        return makeErrorVo(errorCode, path, includeStackTrace ? convertStackTrace(th) : "");
    }

    private BizErrorVo makeErrorVo(String errorCode,
                                   String path,
                                   String trace) {

        return BizErrorVo.of(errorCode, path, trace);
    }

    public String convertStackTrace(Throwable th) {
        if (th == null) return "";

        StringWriter writer = new StringWriter();
        th.printStackTrace(new PrintWriter(writer));

        return writer.toString();
    }

}
