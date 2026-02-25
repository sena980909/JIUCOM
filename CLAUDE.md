# JIUCOM (지우컴)

컴퓨터 부품 가격비교/견적/커뮤니티 플랫폼 API

## 기술 스택

- Java 17, Spring Boot 3.4.2, Gradle Kotlin DSL
- MySQL 8.0 (prod), H2 (dev/test), Redis 7
- QueryDSL 5.1.0, Flyway, SpringDoc OpenAPI
- JWT (jjwt 0.12.6), Spring Security, WebSocket

## 빌드 & 실행

```bash
# 빌드 (테스트 제외)
./gradlew clean build -x test

# Dev 프로필 실행 (H2, Redis 불필요)
./gradlew bootRun

# Docker 풀스택 실행
docker compose up -d
```

## 프로젝트 구조

```
src/main/java/com/jiucom/api/
├── JiucomApplication.java
├── global/
│   ├── config/          # Spring 설정 (Security, Redis, QueryDSL, Swagger, WebSocket, WebMvc)
│   ├── entity/          # BaseTimeEntity (createdAt, updatedAt, isDeleted, softDelete)
│   ├── exception/       # GlobalException, ExceptionAdvice, ErrorCode
│   ├── jwt/             # JwtTokenProvider, JwtAuthenticationFilter
│   ├── response/        # ApiResponse<T>
│   └── util/            # RedisUtil
├── domain/
│   ├── user/            # User, RefreshToken, Auth
│   ├── part/            # Part (JSON specs), 검색(QueryDSL)
│   ├── build/           # Build, BuildPart, CompatibilityService
│   ├── seller/          # Seller
│   ├── price/           # PriceEntry, PriceHistory, PriceAlert, Crawler, Scheduler
│   ├── post/            # Post (BoardType)
│   ├── comment/         # Comment (대댓글 지원)
│   ├── review/          # Review
│   ├── favorite/        # Favorite
│   ├── notification/    # Notification (WebSocket), Scheduler
│   └── admin/           # AdminController
```

## 컨벤션

- **패키지 구조**: `domain/{feature}/{layer}` (entity, repository, service, controller)
- **Soft Delete**: 모든 엔티티는 `BaseTimeEntity` 상속 (isDeleted + deletedAt)
- **API 응답**: `ApiResponse<T>` 래퍼 사용
- **에러코드**: `JIUCOM-` 접두사 (예: `JIUCOM-U001`)
- **프로필**: dev(H2, Flyway off), prod(MySQL, Flyway on), test(H2 인메모리)
- **context-path**: `/api/v1`

## 주요 URL

- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- Actuator Health: `http://localhost:8080/api/v1/actuator/health`
- H2 Console (dev): `http://localhost:8080/api/v1/h2-console`
