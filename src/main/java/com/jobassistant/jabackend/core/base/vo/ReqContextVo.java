package com.jobassistant.jabackend.core.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jobassistant.jabackend.core.utils.IpUtils;
import com.jobassistant.jabackend.core.utils.UuidUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReqContextVo extends BaseVo {

    private String requestId;
    private String requestUri;
    private String clientIp;
    private HttpMethod httpMethod;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTimestamp;

    public static ReqContextVo from(HttpServletRequest request) {
        return new ReqContextVo(request.getRequestURI(),
                IpUtils.getClientIp(request),
                HttpMethod.valueOf(request.getMethod()));
    }

    private ReqContextVo(String requestUri,
                         String clientIp,
                         HttpMethod httpMethod) {

        this.requestId = UuidUtils.generate();
        this.requestTimestamp = LocalDateTime.now();
        this.requestUri = requestUri;
        this.clientIp = clientIp;
        this.httpMethod = httpMethod;
    }
}
