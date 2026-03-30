package com.jobassistant.jabackend.core.base.vo;

import com.jobassistant.jabackend.core.base.dto.BaseReqDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BizPageableDto extends BaseReqDto {
    private boolean pagingYn;
}
