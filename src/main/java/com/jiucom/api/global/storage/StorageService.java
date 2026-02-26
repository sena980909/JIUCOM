package com.jiucom.api.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(MultipartFile file, String directory);

    void delete(String url);
}
