package com.jobassistant.jabackend.infra.file;

import com.jobassistant.jabackend.core.base.exception.BizException;
import com.jobassistant.jabackend.core.exception.constants.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final FileStorageProperties properties;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.FILE_STORAGE_INIT_FAILED.getErrorCode(),
                    "파일 저장 루트 디렉토리 생성 실패: " + rootLocation);
        }
        log.info("파일 저장 루트 디렉토리: {}", rootLocation);
    }

    @Override
    public String store(MultipartFile file) {
        return storeInternal(file, null);
    }

    @Override
    public String store(MultipartFile file, String subDirectory) {
        return storeInternal(file, subDirectory);
    }

    @Override
    public void delete(String relativePath) {
        Path target = rootLocation.resolve(relativePath).normalize();
        assertWithinRoot(target);
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.FILE_DELETE_FAILED.getErrorCode(),
                    "파일 삭제 실패: " + relativePath);
        }
    }

    @Override
    public Path resolve(String relativePath) {
        Path target = rootLocation.resolve(relativePath).normalize();
        assertWithinRoot(target);
        return target;
    }

    // ── private ──────────────────────────────────────────────────────────────

    private String storeInternal(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BizException(HttpStatus.BAD_REQUEST,
                    ErrorCode.FILE_EMPTY.getErrorCode(), "저장할 파일이 비어 있습니다.");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"
        );
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        Path directory = (subDirectory != null && !subDirectory.isBlank())
                ? rootLocation.resolve(subDirectory).normalize()
                : rootLocation;

        assertWithinRoot(directory);

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.FILE_STORAGE_INIT_FAILED.getErrorCode(),
                    "서브 디렉토리 생성 실패: " + directory);
        }

        Path targetPath = directory.resolve(storedFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.FILE_STORE_FAILED.getErrorCode(),
                    "파일 저장 실패: " + originalFilename);
        }

        String relativePath = rootLocation.relativize(targetPath).toString();
        log.debug("파일 저장 완료: {} → {}", originalFilename, relativePath);
        return relativePath;
    }

    private void assertWithinRoot(Path path) {
        if (!path.startsWith(rootLocation)) {
            throw new BizException(HttpStatus.BAD_REQUEST,
                    ErrorCode.FILE_INVALID_PATH.getErrorCode(),
                    "허용되지 않은 파일 경로입니다.");
        }
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0 && dotIndex < filename.length() - 1)
                ? filename.substring(dotIndex + 1)
                : "";
    }
}
