package com.jiucom.api.global.storage;

import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalStorageServiceTest {

    private LocalStorageService storageService;
    private Path testDir;

    @BeforeEach
    void setUp() throws Exception {
        storageService = new LocalStorageService();
        testDir = Files.createTempDirectory("jiucom-test-uploads");
        Field f = LocalStorageService.class.getDeclaredField("uploadDir");
        f.setAccessible(true);
        f.set(storageService, testDir.toString());
        storageService.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp directory
        Files.walk(testDir)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                });
    }

    @Test
    @DisplayName("이미지 업로드 - 성공")
    void upload_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "fake image data".getBytes());

        String url = storageService.upload(file, "profile");

        assertThat(url).startsWith("/images/profile/");
        assertThat(url).endsWith(".png");

        // Verify file exists on disk
        String relativePath = url.replace("/images/", "");
        Path filePath = testDir.resolve(relativePath);
        assertThat(Files.exists(filePath)).isTrue();
    }

    @Test
    @DisplayName("이미지 업로드 - 빈 파일 거부")
    void upload_emptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> storageService.upload(file, "profile"))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.IMAGE_EMPTY));
    }

    @Test
    @DisplayName("이미지 업로드 - 잘못된 파일 타입 거부")
    void upload_invalidType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "not an image".getBytes());

        assertThatThrownBy(() -> storageService.upload(file, "profile"))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.IMAGE_INVALID_TYPE));
    }

    @Test
    @DisplayName("이미지 삭제 - 성공")
    void delete_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake image".getBytes());
        String url = storageService.upload(file, "profile");

        String relativePath = url.replace("/images/", "");
        Path filePath = testDir.resolve(relativePath);
        assertThat(Files.exists(filePath)).isTrue();

        storageService.delete(url);
        assertThat(Files.exists(filePath)).isFalse();
    }
}
