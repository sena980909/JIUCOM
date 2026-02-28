# JIUCOM (지우컴)

> 컴퓨터 부품 가격비교 / 견적 / 커뮤니티 풀스택 플랫폼

## 개요

지우컴은 여러 쇼핑몰의 컴퓨터 부품 가격을 한눈에 비교하고, 나만의 PC 견적을 만들 수 있는 웹 서비스입니다.
부품 호환성 자동 검사, 가격 변동 알림, 커뮤니티, 소셜 로그인, 실시간 알림 기능을 제공합니다.

## 라이브 데모

| 서비스 | URL |
|--------|-----|
| 프론트엔드 | https://dbbbjuenom6zb.cloudfront.net |
| API Health | http://43.200.200.69/api/v1/actuator/health |
| Swagger UI | http://43.200.200.69/api/v1/swagger-ui/index.html |

## 기술 스택

### Backend

| 분류 | 기술 |
|------|------|
| Language / Framework | Java 17, Spring Boot 3.4.2, Gradle Kotlin DSL |
| Database | MySQL 8.0 (prod), H2 (dev/test), Redis 7 |
| ORM / Migration | Spring Data JPA, QueryDSL 5.1.0, Flyway |
| 인증 / 보안 | Spring Security, JWT (jjwt 0.12.6), OAuth2 (구글/네이버) |
| 실시간 | WebSocket (STOMP), SimpMessagingTemplate |
| API 문서 | SpringDoc OpenAPI 2.7.0 (Swagger UI) |
| 모니터링 | Micrometer + Prometheus + Grafana |
| 분산 추적 | Zipkin + Micrometer Tracing (Brave) |
| API Gateway | Nginx (리버스 프록시, Rate Limiting) |
| CI/CD | GitHub Actions, Docker Compose |

### Frontend

| 분류 | 기술 |
|------|------|
| Framework | React 19, TypeScript, Vite 6 |
| 스타일링 | Tailwind CSS 4 |
| 라우팅 | React Router v7 (23 페이지) |
| 상태 관리 | @tanstack/react-query (서버 상태), Context API (인증) |
| HTTP | Axios (JWT 인터셉터, 자동 리프레시) |
| 차트 | Recharts (가격 히스토리) |
| 실시간 | SockJS + @stomp/stompjs (WebSocket 알림) |
| 알림 | react-hot-toast |

### 인프라 / 배포

| 분류 | 기술 |
|------|------|
| 클라우드 | AWS (EC2, RDS, S3, CloudFront) |
| 컨테이너 | Docker, Docker Compose |
| 오케스트레이션 | Kubernetes (k8s/ 매니페스트 29개) |
| CDN | CloudFront (HTTPS, SPA 라우팅) |
| DB | RDS MySQL 8.0 (db.t3.micro, 20GB) |

## 아키텍처

### AWS 프로덕션 배포

```
[사용자 브라우저]
       │
       ├── CloudFront (HTTPS CDN)
       │      └── S3 버킷 (React 정적 파일)
       │
       └── EC2 t3.micro (Docker Compose)
              ├── Nginx (:80, 리버스 프록시)
              ├── Spring Boot API (:8080)
              └── Redis 7 (캐시/Rate Limit)
                     │
                     └── RDS db.t3.micro (MySQL 8.0, 20GB)
```

### 로컬 개발 (Docker Compose 9서비스)

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

## 빠른 시작

### 백엔드 개발 (H2 인메모리, Redis 불필요)

```bash
# 빌드 + 테스트
./gradlew clean build

# 실행 (dev 프로필)
./gradlew bootRun
```

### 프론트엔드 개발

```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

### Docker 풀스택 (9개 서비스)

```bash
# 전체 스택 기동
docker compose up -d --build

# 종료
docker compose down
```

### 로컬 접속 URL

| 서비스 | URL | 비고 |
|--------|-----|------|
| 프론트엔드 | http://localhost:5173 | Vite 개발 서버 |
| Swagger UI | http://localhost/api/v1/swagger-ui.html | API 문서 |
| API Health | http://localhost/api/v1/actuator/health | 상태 확인 |
| Grafana | http://localhost:3000/grafana/ | admin / admin |
| Prometheus | http://localhost:9090 | 메트릭 타겟 확인 |
| Zipkin | http://localhost:9411 | 분산 추적 UI |
| H2 Console | http://localhost:8080/api/v1/h2-console | dev 프로필만 |

## 프로젝트 구조

### Backend

```
src/main/java/com/jiucom/api/
├── JiucomApplication.java
├── global/
│   ├── config/          # Security, Redis, QueryDSL, Swagger, WebSocket, WebMvc
│   │   └── interceptor/ # RateLimitInterceptor (dev), RedisRateLimitInterceptor (prod)
│   ├── entity/          # BaseTimeEntity (createdAt, updatedAt, isDeleted, softDelete)
│   ├── exception/       # GlobalException, ExceptionAdvice, ErrorCode
│   ├── jwt/             # JwtTokenProvider, JwtAuthenticationFilter
│   ├── oauth2/          # OAuth2 소셜 로그인 (구글/네이버)
│   ├── response/        # ApiResponse<T>
│   ├── util/            # RedisUtil
│   ├── storage/         # StorageService (Local/S3), StorageConfig
│   ├── email/           # EmailService (Mock/SMTP), EmailConfig
│   ├── naver/           # 네이버 쇼핑 API (부품 가격 자동 수집)
│   ├── logging/         # RequestLoggingFilter
│   └── actuator/        # CustomMetricsConfig
├── domain/
│   ├── user/            # User, RefreshToken, Auth (JWT + OAuth2 인증)
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
```

### Frontend

```
frontend/src/
├── main.tsx              # 엔트리포인트 (BrowserRouter, QueryClient, AuthProvider)
├── App.tsx               # 라우트 설정 (23 라우트)
├── api/                  # Axios API 클라이언트 (16 모듈)
│   ├── client.ts         # Axios 인스턴스 (JWT 인터셉터, 401 자동 리프레시)
│   ├── auth.ts           # 회원가입, 로그인, 소셜 로그인 URL
│   ├── parts.ts          # 부품 검색, 상세, 카테고리
│   ├── builds.ts         # 견적 CRUD
│   ├── prices.ts         # 가격 비교, 이력, 알림
│   ├── posts.ts          # 게시글 CRUD
│   ├── comments.ts       # 댓글/대댓글 CRUD
│   ├── reviews.ts        # 리뷰 CRUD
│   ├── favorites.ts      # 찜하기
│   ├── likes.ts          # 좋아요 토글
│   ├── search.ts         # 통합 검색, 자동완성
│   ├── users.ts          # 프로필
│   ├── notifications.ts  # 알림
│   ├── images.ts         # 이미지 업로드
│   ├── sellers.ts        # 판매처
│   └── admin.ts          # 관리자
├── contexts/
│   └── AuthContext.tsx    # JWT 토큰 + 유저 정보 전역 관리
├── utils/
│   └── specsHelper.ts    # 카테고리별 스펙 요약 추출 유틸
├── hooks/
│   ├── useAuth.ts        # 인증 상태 훅
│   ├── useWebSocket.ts   # 실시간 알림 (STOMP over SockJS)
│   └── useDebounce.ts    # 검색 디바운스
├── components/
│   ├── layout/           # Header, Footer, Sidebar, Layout
│   ├── common/           # Pagination, Modal, SearchBar, ImageUpload, ProtectedRoute
│   ├── parts/            # PartCard, PartFilter, PriceChart
│   ├── builds/           # BuildCard, PartSelectorModal, SelectedPartCard, CompatibilityWarning
│   ├── posts/            # PostCard, PostEditor, CommentTree
│   └── reviews/          # ReviewCard
└── pages/
    ├── Home.tsx           # 랜딩 (인기 부품, 최신글, 인기 견적)
    ├── auth/              # Login, Signup, OAuthCallback
    ├── parts/             # PartList, PartDetail (가격 차트 + 리뷰)
    ├── builds/            # BuildList, BuildDetail, BuildEditor
    ├── posts/             # PostList, PostDetail, PostEditor
    ├── search/            # SearchResults (통합 검색)
    ├── sellers/           # SellerList
    ├── user/              # Profile, MyBuilds, MyFavorites, Notifications
    └── admin/             # Dashboard, UserManagement, PartManagement
```

### 인프라

```
├── docker-compose.yml        # 로컬 9개 서비스 오케스트레이션
├── docker-compose.aws.yml    # AWS 배포용 (app + redis + nginx)
├── Dockerfile                # 백엔드 멀티스테이지 빌드 (JDK 17)
├── frontend/Dockerfile       # 프론트엔드 (nginx 정적 서빙)
├── nginx/
│   ├── nginx.conf            # 로컬 API Gateway
│   └── aws.conf              # AWS용 리버스 프록시
├── scripts/
│   ├── setup-ec2.sh          # EC2 초기 설정 (Docker, Git)
│   ├── deploy-backend.sh     # 백엔드 배포 + 헬스체크
│   ├── deploy-frontend-s3.sh # 프론트 빌드 → S3 업로드
│   └── deploy-all.sh         # 전체 배포 한번에
├── k8s/                      # Kubernetes 매니페스트 (29 yaml)
├── .env.aws.example          # AWS 환경변수 템플릿
└── AWS_DEPLOY_GUIDE.md       # AWS 배포 매뉴얼
```

## 페이지 목록 (23 페이지)

| 라우트 | 페이지 | 인증 | 설명 |
|--------|--------|------|------|
| `/` | Home | - | 인기 부품, 최신글, 인기 견적 |
| `/login` | Login | - | 이메일 + 구글/네이버 소셜 로그인 |
| `/signup` | Signup | - | 회원가입 |
| `/oauth/callback` | OAuthCallback | - | 소셜 로그인 콜백 |
| `/parts` | PartList | - | 카테고리 필터, 검색, 정렬 |
| `/parts/:id` | PartDetail | - | 가격 비교, 차트, 리뷰, 즐겨찾기 |
| `/builds` | BuildList | - | 공개 견적 목록 |
| `/builds/new` | BuildEditor | △ | 견적 빌더 (이미지+스펙+추천순 모달, 비로그인 부품 선택, 저장만 로그인) |
| `/builds/:id` | BuildDetail | - | 견적 상세 (부품 목록, 총 가격) |
| `/builds/:id/edit` | BuildEditor | O | 견적 수정 |
| `/posts` | PostList | - | BoardType 탭 (자유/QNA/리뷰/공지) |
| `/posts/new` | PostEditor | O | 글쓰기 |
| `/posts/:id` | PostDetail | - | 본문 + 댓글 + 좋아요 |
| `/posts/:id/edit` | PostEditor | O | 글 수정 |
| `/search` | SearchResults | - | 통합 검색 (부품 + 게시글 + 견적) |
| `/sellers` | SellerList | - | 판매처 목록 |
| `/profile` | Profile | O | 프로필 수정 |
| `/my/builds` | MyBuilds | O | 내 견적 목록 |
| `/my/favorites` | MyFavorites | O | 즐겨찾기 |
| `/notifications` | Notifications | O | 실시간 알림 목록 |
| `/admin` | Dashboard | ADMIN | 통계 대시보드 |
| `/admin/users` | UserManagement | ADMIN | 회원 관리 |
| `/admin/parts` | PartManagement | ADMIN | 부품 CRUD |

## API 엔드포인트 (70+)

모든 엔드포인트는 `/api/v1` 접두사를 사용합니다.

### 인증 / 회원

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| POST | /auth/signup | 회원가입 (즉시 토큰 발급) |
| POST | /auth/login | 로그인 |
| POST | /auth/refresh | 토큰 갱신 (로테이션) |
| POST | /auth/logout | 로그아웃 |
| GET | /auth/oauth2/urls | OAuth2 소셜 로그인 URL |
| GET | /users/me | 내 프로필 조회 |
| PATCH | /users/me | 프로필 수정 |

### 부품 / 가격

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | /parts | 부품 검색 (QueryDSL 동적 필터, 추천순/가격순 정렬) |
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

## AWS 배포

### 리소스 구성

| 리소스 | 스펙 | 비용 (프리티어) |
|--------|------|----------------|
| EC2 | t3.micro (1vCPU, 1GB), Elastic IP: 43.200.200.69 | $0/월 |
| RDS | db.t3.micro MySQL 8.0, 20GB gp2 | $0/월 |
| S3 | jiucom-frontend-app (정적 웹 호스팅) | $0/월 |
| CloudFront | E3SUNW1I4FKFCM (HTTPS CDN, SPA 라우팅) | $0/월 |
| EBS | 20GB gp3 (EC2 스토리지) | $0/월 |

### 배포 방법

**백엔드 (EC2 Instance Connect + Docker Compose)**

```bash
# 1. git push
git push origin master

# 2. EC2 Instance Connect로 접속 & 배포 (SSH 키 불필요)
TEMP_DIR=$(mktemp -d) && ssh-keygen -t rsa -b 2048 -f "$TEMP_DIR/ec2_key" -N "" -q
PUB_KEY=$(cat "$TEMP_DIR/ec2_key.pub")
aws ec2-instance-connect send-ssh-public-key \
  --instance-id i-033eb104d2946dcc7 \
  --instance-os-user ec2-user \
  --ssh-public-key "$PUB_KEY"
ssh -i "$TEMP_DIR/ec2_key" -o StrictHostKeyChecking=no ec2-user@43.200.200.69 \
  "cd /home/ec2-user/jiucom && git pull origin master && bash scripts/deploy-backend.sh"
```

**프론트엔드 (S3 + CloudFront)**

```bash
cd frontend && npm run build
aws s3 sync dist/ s3://jiucom-frontend-app --delete \
  --cache-control "public, max-age=31536000" \
  --exclude "index.html" --exclude "*.json"
aws s3 cp dist/index.html s3://jiucom-frontend-app/index.html \
  --cache-control "public, max-age=0, must-revalidate" --content-type "text/html"
aws cloudfront create-invalidation --distribution-id E3SUNW1I4FKFCM --paths "/*"
```

자세한 내용은 [AWS_DEPLOY_GUIDE.md](./AWS_DEPLOY_GUIDE.md) 참고.

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

## Redis 캐싱

주요 페이지에 Redis 캐싱을 적용하여 응답 속도를 개선합니다. Redis 장애 시 자동으로 DB fallback합니다.

| 대상 | 캐시 키 | TTL | 무효화 시점 |
|------|---------|-----|------------|
| 게시글 상세 | `post:detail:{id}` | 10분 | 수정/삭제 시 |
| 게시글 목록 | `post:list:{boardType}:{page}:{size}` | 5분 | 작성/수정/삭제 시 |
| 부품 상세 | `part:detail:{id}` | 30분 | 수정/삭제 시 |
| 부품 카테고리 | `part:categories` | 24시간 | 거의 불변 |
| 댓글 목록 | `comment:list:{postId}:{page}:{size}` | 5분 | 댓글 CUD 시 |

## 보안

| 항목 | 설정 |
|------|------|
| CORS | dev: `*`, prod: 환경변수 (`CORS_ORIGINS`) |
| Rate Limit | dev: in-memory, prod: Redis (분당 100회) |
| Security Headers | HSTS, CSP, X-Content-Type-Options, Referrer-Policy |
| JWT | Access 1h, Refresh 7d (로테이션) |
| OAuth2 | 구글, 네이버 소셜 로그인 |
| Nginx | Rate Limit (30 req/sec), X-Frame-Options, XSS Protection |

## 테스트

```bash
# 전체 테스트 (110개)
./gradlew test
```

- 단위 테스트: JUnit 5 + Mockito (Service, Controller)
- 통합 테스트: @SpringBootTest + TestRestTemplate (Auth, Post/Comment, Like)
- OAuth2 테스트: CustomOAuth2UserService, OAuth2SuccessHandler, OAuth2Integration
- 110 tests, 0 failures

## CI/CD

- **GitHub Actions**: push/PR 시 자동 빌드 + 테스트 (`.github/workflows/ci.yml`)
- **Flyway**: V1 (초기 스키마), V2 (좋아요 + 검색 인덱스), V3 (결제 테이블), V4 (중복 인덱스 정리), V5 (인기도 점수 + 추천순 정렬)
- **Docker**: 멀티스테이지 빌드, JVM container-aware 메모리 설정 (G1GC, MaxRAMPercentage=55%)

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
| NAVER_SHOPPING_CLIENT_ID | - | 네이버 쇼핑 API 클라이언트 ID |
| NAVER_SHOPPING_CLIENT_SECRET | - | 네이버 쇼핑 API 시크릿 |

## 외부 API 연동

### 네이버 쇼핑 API

네이버 검색 API를 통해 실제 컴퓨터 부품 가격 데이터를 자동 수집합니다.

| 항목 | 내용 |
|------|------|
| API | 네이버 검색 > 쇼핑 (v1/search/shop.json) |
| 카테고리 | CPU, GPU, RAM, SSD, HDD, 메인보드, 파워, 케이스, 쿨러 (9종) |
| 필터링 | 공통 블랙리스트 (30+ 키워드) + 카테고리별 블랙리스트 (조립PC, 노트북, 중고, 청소솔 등 자동 제외) |
| 자동 처리 | 부품 생성/업데이트, 판매자 등록, 가격 이력 기록 |

```bash
# 관리자 API로 전체 임포트
curl -X POST http://localhost:8080/api/v1/admin/naver/import/all \
  -H "Authorization: Bearer {admin-token}"

# 카테고리별 임포트
curl -X POST http://localhost:8080/api/v1/admin/naver/import/GPU \
  -H "Authorization: Bearer {admin-token}"
```

## 주요 기능 상세

### 견적 빌더 (다나와 스타일)

부품 카테고리별 시각적 선택 모달을 통해 PC 견적을 구성합니다.

- **부품 선택 모달**: 이미지 + 제조사 + 가격이 포함된 2열 카드 그리드
- **정렬 옵션**: 추천순 (인기도 점수 기반) / 낮은가격순 / 높은가격순
- **검색**: 부품명 실시간 검색 (300ms 디바운스)
- **스펙 표시**: 선택된 부품의 핵심 사양 자동 요약 (예: "8코어 / 16스레드 / AM5 / 120W")
- **비로그인 사용 가능**: 부품 선택은 자유, 저장만 로그인 필요

### 인기도 점수 시스템

부품별 인기도 점수를 자동 산출하여 추천순 정렬에 활용합니다.

| 기준 | 점수 |
|------|------|
| 메인스트림 가격대 | 50점 (카테고리별 기준 상이) |
| 하이엔드 가격대 | 40점 |
| 보급형 가격대 | 30점 |
| 구형/초저가 | 10점 |
| 유명 브랜드 보너스 | +20점 (AMD Ryzen, Intel Core, ASUS, MSI 등) |
| 현세대 모델 보너스 | +10점 (RTX 5060, Ryzen 9600X, i5-14600K 등) |

## 문서

- **[AWS_DEPLOY_GUIDE.md](./AWS_DEPLOY_GUIDE.md)** - AWS 프리티어 배포 매뉴얼
- **[JIUCOM_MSA_Manual.pdf](./JIUCOM_MSA_Manual.pdf)** - MSA 인프라 동작 원리 매뉴얼 (한글)
- **Swagger UI** - http://43.200.200.69/api/v1/swagger-ui/index.html (라이브)
