package com.jiucom.api.global.storage;

import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

public class LocalStorageService implements StorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${storage.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패: " + uploadDir, e);
        }
    }

    @Override
    public String upload(MultipartFile file, String directory) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = UUID.randomUUID() + extension;

        try {
            Path dirPath = Paths.get(uploadDir, directory);
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());
            return "/images/" + directory + "/" + filename;
        } catch (IOException e) {
            throw new GlobalException(GlobalErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || !url.startsWith("/images/")) {
            return;
        }
        String relativePath = url.replace("/images/", "");
        Path filePath = Paths.get(uploadDir, relativePath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new GlobalException(GlobalErrorCode.IMAGE_EMPTY);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new GlobalException(GlobalErrorCode.IMAGE_INVALID_TYPE);
        }
    }
}
