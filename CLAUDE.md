# JIUCOM (지우컴)

컴퓨터 부품 가격비교/견적/커뮤니티 플랫폼 API

## 기술 스택

- Java 17, Spring Boot 3.4.2, Gradle Kotlin DSL
- MySQL 8.0 (prod), H2 (dev/test), Redis 7
- QueryDSL 5.1.0, Flyway, SpringDoc OpenAPI
- JWT (jjwt 0.12.6), Spring Security, WebSocket
- Micrometer + Prometheus, Logback (profile-based)

## 빌드 & 실행

```bash
# 빌드 (테스트 포함)
./gradlew clean build

# 빌드 (테스트 제외)
./gradlew clean build -x test

# Dev 프로필 실행 (H2, Redis 불필요)
./gradlew bootRun

# 전체 테스트 실행
./gradlew test

# Docker 풀스택 실행
docker compose up -d
```

## 프로젝트 구조

```
src/main/java/com/jiucom/api/
├── JiucomApplication.java
├── global/
│   ├── config/          # Security, Redis, QueryDSL, Swagger, WebSocket, WebMvc
│   │   └── interceptor/ # RateLimitInterceptor (dev), RedisRateLimitInterceptor (prod)
│   ├── entity/          # BaseTimeEntity (createdAt, updatedAt, isDeleted, softDelete)
│   ├── exception/       # GlobalException, ExceptionAdvice, ErrorCode
│   ├── jwt/             # JwtTokenProvider, JwtAuthenticationFilter
│   ├── response/        # ApiResponse<T>
│   ├── util/            # RedisUtil
│   ├── storage/         # StorageService (Local/S3), StorageConfig
│   ├── email/           # EmailService (Mock/SMTP), EmailConfig
│   ├── logging/         # RequestLoggingFilter
│   └── actuator/        # CustomMetricsConfig
├── domain/
│   ├── user/            # User, RefreshToken, Auth
│   ├── part/            # Part (JSON specs), 검색(QueryDSL)
│   ├── build/           # Build, BuildPart, CompatibilityService
│   ├── seller/          # Seller
│   ├── price/           # PriceEntry, PriceHistory, PriceAlert, Crawler, Scheduler
│   ├── post/            # Post (BoardType), QueryDSL search
│   ├── comment/         # Comment (대댓글 지원)
│   ├── review/          # Review
│   ├── favorite/        # Favorite
│   ├── like/            # ContentLike (POST/COMMENT/REVIEW/BUILD)
│   ├── search/          # 통합 검색 (부품+게시글+견적)
│   ├── image/           # 이미지 업로드
│   ├── notification/    # Notification (WebSocket), Scheduler
│   ├── payment/         # Payment (준비중)
│   └── admin/           # AdminController, AdminService
```

## 컨벤션

- **패키지 구조**: `domain/{feature}/{layer}` (entity, repository, service, controller)
- **Soft Delete**: 모든 엔티티는 `BaseTimeEntity` 상속 (isDeleted + deletedAt)
- **API 응답**: `ApiResponse<T>` 래퍼 사용
- **에러코드**: `JIUCOM-` 접두사 (예: `JIUCOM-U001`)
- **프로필**: dev(H2, Flyway off), prod(MySQL, Flyway on), test(H2 인메모리)
- **context-path**: `/api/v1`
- **테스트**: JUnit 5 + Mockito (subclass mock maker), @WebMvcTest + @SpringBootTest

## 주요 API 엔드포인트 (70+)

| 도메인 | 주요 엔드포인트 |
|--------|----------------|
| Auth | POST /auth/signup, /auth/login, /auth/refresh, /auth/logout |
| User | GET/PATCH /users/me |
| Part | GET /parts (검색), /parts/{id}, /parts/categories |
| Build | GET/POST/PUT/DELETE /builds |
| Price | GET /prices/compare, /prices/history, POST/GET/DELETE /prices/alerts |
| Seller | GET /sellers |
| Post | GET/POST/PUT/DELETE /posts |
| Comment | GET/POST/PUT/DELETE /posts/{id}/comments |
| Review | GET/POST/PUT/DELETE /reviews |
| Favorite | GET/POST/DELETE /favorites |
| Like | POST/GET /likes/{targetType}/{targetId} |
| Search | GET /search, /search/suggest |
| Image | POST /images/upload, GET /images/{dir}/{filename} |
| Notification | GET /notifications, /notifications/unread-count, PATCH /notifications/read |
| Admin | GET /admin/dashboard, /admin/users, POST/PUT/DELETE /admin/parts |

## 주요 URL

- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- Actuator Health: `http://localhost:8080/api/v1/actuator/health`
- Actuator Metrics: `http://localhost:8080/api/v1/actuator/metrics`
- Actuator Prometheus: `http://localhost:8080/api/v1/actuator/prometheus`
- H2 Console (dev): `http://localhost:8080/api/v1/h2-console`

## 보안

- CORS: dev=`*`, prod=환경변수 (`CORS_ORIGINS`)
- Rate Limit: dev=in-memory, prod=Redis (분당 100회)
- Security Headers: HSTS, CSP, X-Content-Type-Options, Referrer-Policy, Permissions-Policy
- JWT: Access 1h, Refresh 7d (로테이션)

## CI/CD

- GitHub Actions: `.github/workflows/ci.yml` (push/PR → build + test)
- Flyway: V1 (initial schema), V2 (Phase 2 - like + search indexes)
