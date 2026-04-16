package com.jobassistant.jabackend.infra.file;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {

    /**
     * 파일을 uploadDir 루트에 저장한다.
     *
     * @param file 업로드된 파일
     * @return 저장된 파일의 상대 경로 (uploadDir 기준)
     */
    String store(MultipartFile file);

    /**
     * 파일을 uploadDir/{subDirectory} 하위에 저장한다.
     *
     * @param file         업로드된 파일
     * @param subDirectory 하위 디렉토리명 (예: "resumes", "images")
     * @return 저장된 파일의 상대 경로 (uploadDir 기준, 구분자 포함)
     */
    String store(MultipartFile file, String subDirectory);

    /**
     * 상대 경로에 해당하는 파일을 삭제한다.
     *
     * @param relativePath store() 반환값
     */
    void delete(String relativePath);

    /**
     * 상대 경로를 절대 Path 로 변환한다.
     *
     * @param relativePath store() 반환값
     * @return 절대 경로
     */
    Path resolve(String relativePath);
}
