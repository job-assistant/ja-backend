package com.jobassistant.jabackend.core.base.dto;

import com.jobassistant.jabackend.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseReqDto extends BaseVo {
    @Schema(hidden = true)
    private String reqUserId;
}
