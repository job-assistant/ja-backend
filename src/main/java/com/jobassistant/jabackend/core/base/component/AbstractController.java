package com.jobassistant.jabackend.core.base.component;

import com.jobassistant.jabackend.core.base.vo.BizRespVo;
import com.jobassistant.jabackend.core.base.vo.GenericListVo;
import org.springframework.http.HttpStatus;

import java.util.List;


public abstract class AbstractController {

    protected final <T> BizRespVo<T> makeResponse(T data) {
        return BizRespVo.withStatus(HttpStatus.OK, data);
    }

    protected <T> BizRespVo<GenericListVo<T>> makeGenericListResponse(List<T> list) {

        return makeResponse(new GenericListVo<>(list));
    }


    protected final <T> BizRespVo<T> makeResponse(HttpStatus status, T data) {
        return BizRespVo.withStatus(status, data);
    }

    protected final <T> BizRespVo<T> makeCreatedResponse(T data) {
        return BizRespVo.withStatus(HttpStatus.CREATED, data);
    }
}
