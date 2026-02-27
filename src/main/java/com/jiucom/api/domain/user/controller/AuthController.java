package com.jiucom.api.domain.user.controller;

import com.jiucom.api.domain.user.dto.request.LoginRequest;
import com.jiucom.api.domain.user.dto.request.RefreshRequest;
import com.jiucom.api.domain.user.dto.request.SignupRequest;
import com.jiucom.api.domain.user.dto.response.JwtTokenResponse;
import com.jiucom.api.domain.user.service.AuthService;
import com.jiucom.api.global.response.ApiResponse;
import com.jiucom.api.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        JwtTokenResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtTokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        JwtTokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "소셜 로그인 URL 목록", description = "카카오/네이버/구글 OAuth2 로그인 URL을 반환합니다.")
    @GetMapping("/oauth2/urls")
    public ResponseEntity<ApiResponse<Map<String, String>>> getOAuth2Urls(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443
                ? ":" + request.getServerPort() : "");
        String contextPath = request.getContextPath();

        Map<String, String> urls = new LinkedHashMap<>();
        urls.put("google", baseUrl + contextPath + "/oauth2/authorize/google");
        urls.put("kakao", baseUrl + contextPath + "/oauth2/authorize/kakao");
        urls.put("naver", baseUrl + contextPath + "/oauth2/authorize/naver");

        return ResponseEntity.ok(ApiResponse.ok(urls));
    }
}
