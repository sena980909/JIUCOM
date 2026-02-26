# JIUCOM (지우컴)

> 컴퓨터 부품 가격비교 / 견적 / 커뮤니티 플랫폼 API

## 개요

지우컴은 여러 쇼핑몰의 컴퓨터 부품 가격을 한눈에 비교하고, 나만의 PC 견적을 만들 수 있는 웹 서비스의 백엔드 API입니다.
부품 호환성 자동 검사, 가격 변동 알림, 커뮤니티 기능을 제공합니다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language / Framework | Java 17, Spring Boot 3.4.2, Gradle Kotlin DSL |
| Database | MySQL 8.0 (prod), H2 (dev/test), Redis 7 |
| ORM / Migration | Spring Data JPA, QueryDSL 5.1.0, Flyway |
| 인증 / 보안 | Spring Security, JWT (jjwt 0.12.6), BCrypt |
| 실시간 | WebSocket (STOMP), SimpMessagingTemplate |
| API 문서 | SpringDoc OpenAPI 2.7.0 (Swagger UI) |
| 모니터링 | Micrometer + Prometheus + Grafana |
| 분산 추적 | Zipkin + Micrometer Tracing (Brave) |
| API Gateway | Nginx (리버스 프록시, Rate Limiting) |
| CI/CD | GitHub Actions, Docker Compose |

## 아키텍처

```
                     [클라이언트]
                          |
                     포트 80 (HTTP)
                          |
                    +-----v------+
                    |   Nginx    |  API Gateway
                    +--+---+---+-+
                       |   |   |
          /api/v1/*    |   |   |  /zipkin/*
       +---------------+   |   +-------------+
       |           /grafana/*                 |
       v                   v                  v
  +----+-----+      +-----+-----+     +------+----+
  | App:8080 |      |Grafana:3000|     |Zipkin:9411|
  +--+----+--+      +-----+------+     +-----------+
     |    |               |
     v    v         +-----v--------+
  MySQL  Redis      |Prometheus:9090|
  :3306  :6379      +--+-----+---+-+
                       |     |   |
                     App  Redis  MySQL
                   metrics Exp.  Exp.
                          :9121  :9104
```

### 9개 서비스 구성

| 서비스 | 이미지 | 포트 | 역할 |
|--------|--------|------|------|
| mysql | mysql:8.0 | 3306 | 메인 데이터베이스 |
| redis | redis:7-alpine | 6379 | 캐시 / 세션 / Rate Limit |
| app | Spring Boot (빌드) | 8080 | API 서버 (70+ 엔드포인트) |
| nginx | nginx:alpine | 80 | API Gateway / 리버스 프록시 |
| prometheus | prom/prometheus | 9090 | 메트릭 수집 |
| grafana | grafana/grafana | 3000 | 모니터링 대시보드 |
| redis-exporter | oliver006/redis_exporter | 9121 | Redis 메트릭 익스포터 |
| mysql-exporter | prom/mysqld-exporter | 9104 | MySQL 메트릭 익스포터 |
| zipkin | openzipkin/zipkin | 9411 | 분산 추적 |

## 빠른 시작

### 개발 환경 (H2 인메모리, Redis 불필요)

```bash
# 빌드 + 테스트
./gradlew clean build

# 실행 (dev 프로필)
./gradlew bootRun
```

### Docker 풀스택 (9개 서비스)

```bash
# 전체 스택 기동
docker compose up -d --build

# 종료
docker compose down
```

### 접속 URL

| 서비스 | URL | 비고 |
|--------|-----|------|
| Swagger UI | http://localhost/api/v1/swagger-ui.html | API 문서 |
| API Health | http://localhost/api/v1/actuator/health | 상태 확인 |
| Grafana | http://localhost:3000/grafana/ | admin / admin |
| Prometheus | http://localhost:9090 | 메트릭 타겟 확인 |
| Zipkin | http://localhost:9411 | 분산 추적 UI |
| H2 Console | http://localhost:8080/api/v1/h2-console | dev 프로필만 |

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
│   ├── user/            # User, RefreshToken, Auth (JWT 인증)
│   ├── part/            # Part (JSON specs), 검색 (QueryDSL)
│   ├── build/           # Build, BuildPart, CompatibilityService (호환성 검사)
│   ├── seller/          # Seller (판매처)
│   ├── price/           # PriceEntry, PriceHistory, PriceAlert, Crawler, Scheduler
│   ├── post/            # Post (BoardType), QueryDSL 검색
│   ├── comment/         # Comment (대댓글 지원)
│   ├── review/          # Review (부품 리뷰)
│   ├── favorite/        # Favorite (찜하기)
│   ├── like/            # ContentLike (POST/COMMENT/REVIEW/BUILD 다형성)
│   ├── search/          # 통합 검색 (부품 + 게시글 + 견적) + 자동완성
│   ├── image/           # 이미지 업로드 (Local/S3)
│   ├── notification/    # Notification (WebSocket 실시간 알림)
│   ├── payment/         # Payment (준비중)
│   └── admin/           # AdminController, AdminService (대시보드/회원/부품 관리)

# 인프라 설정
├── docker-compose.yml       # 9개 서비스 오케스트레이션
├── Dockerfile               # 멀티스테이지 빌드 (JDK 17)
├── prometheus.yml            # Prometheus 스크래핑 설정
├── nginx/nginx.conf          # API Gateway 설정
├── monitoring/
│   └── grafana/
│       ├── provisioning/     # 데이터소스 + 대시보드 자동 프로비저닝
│       └── dashboards/       # JIUCOM 대시보드 (16패널)
```

## API 엔드포인트 (70+)

모든 엔드포인트는 `/api/v1` 접두사를 사용합니다.

### 인증 / 회원

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| POST | /auth/signup | 회원가입 (즉시 토큰 발급) |
| POST | /auth/login | 로그인 |
| POST | /auth/refresh | 토큰 갱신 (로테이션) |
| POST | /auth/logout | 로그아웃 |
| GET | /users/me | 내 프로필 조회 |
| PATCH | /users/me | 프로필 수정 |

### 부품 / 가격

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | /parts | 부품 검색 (QueryDSL 동적 필터) |
| GET | /parts/{id} | 부품 상세 (가격 비교 포함) |
| GET | /parts/categories | 카테고리 목록 |
| GET | /prices/compare | 판매처별 가격 비교 |
| GET | /prices/history | 가격 변동 이력 |
| POST | /prices/alerts | 가격 알림 등록 |

### 견적

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | /builds | 공개 견적 목록 |
| GET | /builds/my | 내 견적 목록 |
| POST | /builds | 견적 생성 (호환성 자동 검사) |
| PUT | /builds/{id} | 견적 수정 |
| DELETE | /builds/{id} | 견적 삭제 |

### 커뮤니티

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET/POST | /posts | 게시글 목록/작성 |
| PUT/DELETE | /posts/{id} | 게시글 수정/삭제 |
| GET/POST | /posts/{id}/comments | 댓글 목록/작성 (대댓글 지원) |
| GET/POST | /reviews | 부품 리뷰 목록/작성 |
| POST/DELETE | /favorites | 찜하기 추가/삭제 |
| POST | /likes/{targetType}/{targetId} | 좋아요 토글 |

### 검색 / 알림 / 이미지

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | /search | 통합 검색 (부품+게시글+견적) |
| GET | /search/suggest | 자동완성 |
| GET | /notifications | 알림 목록 |
| PATCH | /notifications/read | 읽음 처리 |
| POST | /images/upload | 이미지 업로드 |

### 관리자

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | /admin/dashboard | 관리자 대시보드 (통계) |
| GET | /admin/users | 회원 목록 |
| POST/PUT/DELETE | /admin/parts | 부품 CRUD |

## 모니터링

### Grafana 대시보드 (16패널)

Docker 기동 시 자동으로 프로비저닝되는 대시보드:

| 섹션 | 패널 | 모니터링 대상 |
|------|------|-------------|
| 개요 | Stat x5 | API 상태, JVM 힙 사용률, 스레드 수, HTTP 요청 속도, 5xx 오류율 |
| HTTP | 시계열 x2 | 평균 응답 시간, 상태코드별 요청 수 |
| JVM | 시계열 x2 | 힙 메모리 추이, GC 일시정지 |
| DB/캐시 | 시계열 x5 | HikariCP 풀, Redis 클라이언트/메모리, MySQL 쿼리/스레드 |

### 분산 추적 (Zipkin)

- dev: 샘플링 100% (모든 요청 추적)
- prod: 샘플링 10% (성능 오버헤드 최소화)
- Zipkin UI에서 요청별 타임라인/워터폴 다이어그램 확인 가능

## 보안

| 항목 | 설정 |
|------|------|
| CORS | dev: `*`, prod: 환경변수 (`CORS_ORIGINS`) |
| Rate Limit | dev: in-memory, prod: Redis (분당 100회) |
| Security Headers | HSTS, CSP, X-Content-Type-Options, Referrer-Policy |
| JWT | Access 1h, Refresh 7d (로테이션) |
| Nginx | Rate Limit (30 req/sec), X-Frame-Options, XSS Protection |

## 테스트

```bash
# 전체 테스트 (96개)
./gradlew test
```

- 단위 테스트: JUnit 5 + Mockito (Service, Controller)
- 통합 테스트: @SpringBootTest + TestRestTemplate (Auth, Post/Comment, Like)
- 96 tests, 0 failures

## CI/CD

- **GitHub Actions**: push/PR 시 자동 빌드 + 테스트 (`.github/workflows/ci.yml`)
- **Flyway**: V1 (초기 스키마), V2 (좋아요 + 검색 인덱스)
- **Docker**: 멀티스테이지 빌드, JVM container-aware 메모리 설정

## 환경변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| DB_HOST | localhost | MySQL 호스트 |
| DB_PORT | 3306 | MySQL 포트 |
| DB_NAME | jiucom | 데이터베이스명 |
| DB_USERNAME | jiucom | DB 사용자 |
| DB_PASSWORD | jiucom1234 | DB 비밀번호 |
| REDIS_HOST | localhost | Redis 호스트 |
| JWT_SECRET | (개발용 기본값) | JWT 서명 키 (64자 이상) |
| CORS_ORIGINS | https://jiucom.com | 허용 도메인 (prod) |
| ZIPKIN_ENDPOINT | http://localhost:9411/api/v2/spans | Zipkin 엔드포인트 |
| GRAFANA_PASSWORD | admin | Grafana 관리자 비밀번호 |

## 문서

- **[JIUCOM_MSA_Manual.pdf](./JIUCOM_MSA_Manual.pdf)** - MSA 인프라 동작 원리 매뉴얼 (한글)
- **Swagger UI** - http://localhost/api/v1/swagger-ui.html (실행 후 접근)
