package com.jobassistant.jabackend.core.base.component;


import com.jobassistant.jabackend.core.base.vo.GenericListVo;

import java.util.List;

public abstract class AbstractService {

    protected <T> GenericListVo<T> makeGenericList(List<T> list) {

        return new GenericListVo<>(list);
    }
}
