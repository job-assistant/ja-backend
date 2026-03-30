package com.jobassistant.jabackend.core.exception;

import com.jobassistant.jabackend.core.base.exception.BizException;
import com.jobassistant.jabackend.core.base.vo.BizErrorVo;
import com.jobassistant.jabackend.core.base.vo.BizRespVo;
import com.jobassistant.jabackend.core.exception.constants.ErrorCode;
import com.jobassistant.jabackend.core.utils.ResponseUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Value("${spring.error.response.include-stack-trace:false}")
    private boolean includeStackTrace;

    private final MessageSource messageSource;

    public String getMessageFromProperties(ErrorCode errorCode,
                                           @Nullable Object[] args) {
        String defaultMessage = "시스템 처리 중 에러가 발생했습니다.";
        String result =
                messageSource.getMessage(errorCode.getErrorCode(), args, defaultMessage, Locale.getDefault());

        if (StringUtils.isBlank(result)) log.warn(">> message code 없음: {}", errorCode.getErrorCode());

        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BizRespVo<BizErrorVo> exception(Exception ex) {

        return ResponseUtils.makeErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex,
                includeStackTrace
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BizRespVo<BizErrorVo> accessDeniedException(AccessDeniedException ex) {

        return ResponseUtils.makeErrorResponse(
                HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.toString(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex,
                includeStackTrace
        );
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<BizRespVo<BizErrorVo>> bizException(BizException ex) {

        BizRespVo<BizErrorVo> body = ResponseUtils.makeErrorResponse(
                ex.getStatus(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex,
                includeStackTrace
        );

        return new ResponseEntity<>(body, ex.getStatus());
    }
}
