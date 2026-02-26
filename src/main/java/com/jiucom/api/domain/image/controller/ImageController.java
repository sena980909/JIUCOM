package com.jiucom.api.domain.image.controller;

import com.jiucom.api.domain.image.dto.response.ImageUploadResponse;
import com.jiucom.api.global.response.ApiResponse;
import com.jiucom.api.global.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 업로드 API")
public class ImageController {

    private final StorageService storageService;

    @Value("${storage.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    @Operation(summary = "이미지 업로드", description = "이미지 파일 업로드 (jpeg, png, webp)")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "general") String directory) {
        String imageUrl = storageService.upload(file, directory);
        return ResponseEntity.ok(ApiResponse.ok(
                ImageUploadResponse.builder().imageUrl(imageUrl).build()));
    }

    @GetMapping("/{directory}/{filename}")
    @Operation(summary = "이미지 조회", description = "업로드된 이미지 파일 서빙 (dev)")
    public ResponseEntity<Resource> getImage(
            @PathVariable String directory,
            @PathVariable String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir, directory, filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "image/jpeg";
        if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".webp")) contentType = "image/webp";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(resource);
    }
}
