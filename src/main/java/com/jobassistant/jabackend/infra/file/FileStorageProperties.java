package com.jobassistant.jabackend.infra.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "job.file")
public class FileStorageProperties {

    /**
     * 파일이 저장될 루트 디렉토리 경로 (절대경로 또는 상대경로)
     * 예) /var/uploads  또는  ./uploads
     */
    private String uploadDir = "./uploads";
}
