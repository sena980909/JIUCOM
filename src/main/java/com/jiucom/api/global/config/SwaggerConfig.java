package com.jiucom.api.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(new Info()
                        .title("JIUCOM API")
                        .description("지우컴 - 컴퓨터 부품 가격비교/견적/커뮤니티 플랫폼 API")
                        .version("v2.0.0"))
                .servers(List.of(
                        new Server().url("/api/v1").description("API Base URL")))
                .tags(List.of(
                        new Tag().name("Auth").description("인증 (회원가입, 로그인, 토큰)"),
                        new Tag().name("User").description("사용자 프로필"),
                        new Tag().name("Part").description("부품 검색/상세"),
                        new Tag().name("Build").description("견적 CRUD"),
                        new Tag().name("Price").description("가격 비교/이력/알림"),
                        new Tag().name("Seller").description("판매처"),
                        new Tag().name("Post").description("게시글"),
                        new Tag().name("Comment").description("댓글/대댓글"),
                        new Tag().name("Review").description("부품 리뷰"),
                        new Tag().name("Favorite").description("관심 부품"),
                        new Tag().name("Like").description("좋아요"),
                        new Tag().name("Search").description("통합 검색"),
                        new Tag().name("Image").description("이미지 업로드"),
                        new Tag().name("Notification").description("알림"),
                        new Tag().name("Admin").description("관리자"),
                        new Tag().name("Payment").description("결제 (준비중)")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
