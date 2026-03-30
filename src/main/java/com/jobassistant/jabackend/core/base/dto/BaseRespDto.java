package com.jobassistant.jabackend.core.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseRespDto {

    //    @Schema(description = "등록자 ID")
//    private String regUserId;
    @Schema(description = "등록일시")
    private LocalDateTime createdAt;
    //    @Schema(description = "수정자 ID")
//    private String updaterId;
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

}