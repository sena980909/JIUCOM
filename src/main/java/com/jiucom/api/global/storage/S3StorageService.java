package com.jiucom.api.global.storage;

import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public class S3StorageService implements StorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${storage.s3.bucket:jiucom-images}")
    private String bucket;

    @Value("${storage.s3.region:ap-northeast-2}")
    private String region;

    @Override
    public String upload(MultipartFile file, String directory) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String key = directory + "/" + UUID.randomUUID() + extension;

        // TODO: Implement S3 upload when AWS SDK is added
        // S3Client s3 = S3Client.builder().region(Region.of(region)).build();
        // s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(file.getContentType()).build(),
        //     RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    @Override
    public void delete(String url) {
        // TODO: Implement S3 delete when AWS SDK is added
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
