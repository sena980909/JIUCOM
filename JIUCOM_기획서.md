# 지우컴 (JIUCOM) - 컴퓨터 견적/부품 비교 플랫폼 기획서

> **Version**: 1.0
> **작성일**: 2026-02-24
> **기반 기술**: UNI_match-Server (Spring Boot 3.4.2 + Java 23)
> **참고**: 다나와(Danawa) 벤치마킹

---

## 1. 프로젝트 개요

### 1.1 서비스 소개
**지우컴(JIUCOM)** 은 컴퓨터 부품 가격 비교, 견적 조립, 호환성 체크, 커뮤니티 기능을 제공하는 원스톱 PC 견적 플랫폼이다. 다나와와 유사하지만 더 직관적인 UX와 AI 기반 추천 기능을 차별점으로 한다.

### 1.2 핵심 가치
| 가치 | 설명 |
|------|------|
| **가격 투명성** | 여러 판매처의 실시간 가격을 한눈에 비교 |
| **호환성 보장** | CPU-메인보드-RAM 등 부품 간 호환성 자동 검증 |
| **쉬운 견적** | 초보자도 드래그 앤 드롭으로 PC 견적 조립 |
| **커뮤니티** | 견적 공유, 리뷰, 질문/답변 생태계 |

### 1.3 타겟 사용자
- **초보 조립러**: PC 조립이 처음인 사용자 → 추천 견적, 호환성 가이드
- **가격 헌터**: 최저가를 찾는 사용자 → 가격 추이, 알림
- **하드코어 유저**: 세밀한 스펙 비교가 필요한 사용자 → 상세 필터, 벤치마크
- **판매자**: 부품 등록 및 가격 관리를 원하는 업체

---

## 2. 기술 스택 (UNI_match-Server 기반)

### 2.1 백엔드
| 기술 | 버전 | 용도 | UNI_match 참고 |
|------|------|------|---------------|
| **Java** | 23 | 메인 언어 | ✅ 동일 |
| **Spring Boot** | 3.4.2 | 프레임워크 | ✅ 동일 |
| **Spring Security** | - | 인증/인가 | ✅ 동일 |
| **Spring Data JPA** | - | ORM | ✅ 동일 |
| **QueryDSL** | 5.0.0 | 동적 쿼리 (부품 필터링 핵심) | ✅ 동일 |
| **JWT (jjwt)** | 0.11.5 | 토큰 인증 | ✅ 동일 |
| **Flyway** | - | DB 마이그레이션 | ✅ 동일 |
| **Swagger (springdoc)** | 2.8.0 | API 문서화 | ✅ 동일 |
| **Lombok** | - | 보일러플레이트 제거 | ✅ 동일 |
| **Spring Validation** | - | 입력 검증 | ✅ 동일 |
| **Spring Actuator** | - | 모니터링 | ✅ 동일 |
| **WebSocket + STOMP** | - | 실시간 가격 알림 | ✅ 동일 |
| **Spring Mail** | - | 이메일 인증/알림 | ✅ 동일 |

### 2.2 데이터베이스 & 캐시
| 기술 | 버전 | 용도 |
|------|------|------|
| **MySQL** | 8.0 | 메인 RDB (부품, 유저, 견적, 주문) |
| **Redis** | 7 (Alpine) | 캐시 (가격 데이터, 인기 검색어, 세션) |
| **H2** | - | 테스트 환경 DB |

### 2.3 인프라 & 배포 (UNI_match Docker 구성 기반)
| 기술 | 용도 |
|------|------|
| **Docker** | 컨테이너화 (멀티스테이지 빌드) |
| **Docker Compose** | 로컬/스테이징 오케스트레이션 |
| **Nginx** | 리버스 프록시 + 프론트엔드 서빙 |
| **AWS S3** | 부품 이미지 저장소 |
| **GitHub Actions** | CI/CD 파이프라인 |

### 2.4 프론트엔드 (별도 레포)
| 기술 | 용도 |
|------|------|
| **React / Next.js** | SPA + SSR (SEO 필수) |
| **TypeScript** | 타입 안전성 |
| **TailwindCSS** | 빠른 UI 개발 |
| **React Query** | 서버 상태 관리 |
| **Zustand** | 클라이언트 상태 관리 |

### 2.5 추가 기술 (지우컴 전용)
| 기술 | 용도 |
|------|------|
| **Elasticsearch** | 부품 검색 엔진 (자동완성, 필터링) |
| **Spring Batch** | 가격 크롤링 배치 처리 |
| **Jsoup** | 판매처 가격 크롤링 |
| **Spring Scheduler** | 주기적 가격 갱신 |

---

## 3. 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │   Web (PC)   │  │ Web (Mobile) │  │  Admin Dashboard │   │
│  │  Next.js SSR │  │  Responsive  │  │     React SPA    │   │
│  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘   │
└─────────┼─────────────────┼───────────────────┼─────────────┘
          │                 │                   │
          ▼                 ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                     Nginx (Reverse Proxy)                    │
│              SSL Termination + Load Balancing                │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                  Spring Boot Application                     │
│                     /api/v1/*                                │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────┐     │
│  │ Controller  │  │  Service   │  │    Repository      │     │
│  │   Layer     │→ │  Layer     │→ │  Layer (JPA+QDsl)  │     │
│  └────────────┘  └────────────┘  └────────────────────┘     │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────┐     │
│  │  Security   │  │ WebSocket  │  │   Batch/Scheduler  │     │
│  │ JWT+OAuth   │  │   STOMP    │  │  가격 크롤링/갱신    │     │
│  └────────────┘  └────────────┘  └────────────────────┘     │
└──────┬──────────────┬───────────────────┬───────────────────┘
       │              │                   │
       ▼              ▼                   ▼
┌────────────┐  ┌──────────┐  ┌────────────────┐
│  MySQL 8.0 │  │ Redis 7  │  │  AWS S3        │
│  메인 DB    │  │ 캐시/세션  │  │  이미지 스토리지 │
└────────────┘  └──────────┘  └────────────────┘
```

---

## 4. 핵심 기능 상세

### 4.1 부품 데이터베이스 (Parts Catalog)

#### 부품 카테고리
| 카테고리 | 주요 스펙 필드 |
|----------|---------------|
| **CPU** | 제조사, 소켓, 코어수, 스레드수, 기본클럭, 부스트클럭, TDP, 내장그래픽 |
| **메인보드** | 소켓, 칩셋, 폼팩터, 메모리슬롯, M.2슬롯, PCIe슬롯 |
| **RAM** | 규격(DDR4/5), 용량, 클럭, 레이턴시, 개수 |
| **GPU** | 칩셋, VRAM, 부스트클럭, TDP, 길이, 전원커넥터 |
| **SSD/HDD** | 타입(NVMe/SATA/HDD), 용량, 읽기속도, 쓰기속도, 폼팩터 |
| **파워서플라이** | 용량(W), 80+ 등급, 모듈러 여부, 크기 |
| **케이스** | 폼팩터 지원, GPU 최대 길이, CPU 쿨러 최대 높이, 팬 슬롯 |
| **쿨러** | 타입(공랭/수랭), 소켓 호환, 높이, 라디에이터 크기 |
| **모니터** | 크기, 해상도, 패널, 주사율, 응답속도 |
| **주변기기** | 키보드, 마우스, 헤드셋 등 |

#### API 엔드포인트
```
GET    /api/v1/parts                       # 부품 목록 (필터+페이징)
GET    /api/v1/parts/{id}                  # 부품 상세
GET    /api/v1/parts/categories            # 카테고리 목록
GET    /api/v1/parts/search                # 통합 검색
GET    /api/v1/parts/{id}/prices           # 가격 이력
GET    /api/v1/parts/{id}/price-comparison # 판매처별 가격 비교
GET    /api/v1/parts/popular               # 인기 부품
GET    /api/v1/parts/ranking               # 카테고리별 랭킹
```

### 4.2 견적 시스템 (Build System)

#### 기능
- **견적 생성/수정/삭제**: 부품을 선택하여 PC 견적 구성
- **호환성 자동 검증**: 소켓, 폼팩터, 전력, 물리적 크기 체크
- **총 가격 계산**: 선택 부품의 최저가 기준 합산
- **견적 공유**: URL 공유, SNS 공유
- **견적 복제**: 다른 사용자 견적을 복제하여 수정
- **추천 견적**: 용도별(게이밍, 사무, 영상편집) 템플릿 제공

#### 호환성 검증 규칙
```
1. CPU 소켓 ↔ 메인보드 소켓 일치
2. RAM 규격 ↔ 메인보드 지원 규격 일치
3. GPU 길이 ≤ 케이스 최대 GPU 길이
4. CPU 쿨러 높이 ≤ 케이스 최대 쿨러 높이
5. 총 TDP ≤ 파워서플라이 용량 × 0.8 (권장)
6. 메인보드 폼팩터 ↔ 케이스 지원 폼팩터
7. M.2 SSD 개수 ≤ 메인보드 M.2 슬롯 수
```

#### API 엔드포인트
```
POST   /api/v1/builds                      # 견적 생성
GET    /api/v1/builds/{id}                 # 견적 상세
PUT    /api/v1/builds/{id}                 # 견적 수정
DELETE /api/v1/builds/{id}                 # 견적 삭제
POST   /api/v1/builds/{id}/clone           # 견적 복제
GET    /api/v1/builds/{id}/compatibility   # 호환성 검증
GET    /api/v1/builds/templates            # 추천 견적 템플릿
GET    /api/v1/builds/popular              # 인기 견적
POST   /api/v1/builds/{id}/share           # 견적 공유 링크 생성
```

### 4.3 가격 비교 시스템 (Price Comparison)

#### 기능
- **실시간 가격 비교**: 쿠팡, 11번가, 컴퓨존, 조이젠 등 판매처 가격
- **가격 추이 그래프**: 일간/주간/월간 가격 변동 차트
- **최저가 알림**: 목표 가격 설정 → WebSocket/이메일 알림
- **가격 예측**: 과거 데이터 기반 가격 추세 분석

#### 가격 크롤링 배치
```
- 실행 주기: 1시간마다 (Spring Scheduler)
- 크롤링 대상: 주요 판매처 5~10곳
- 저장: MySQL (이력) + Redis (최신 가격 캐시)
- 변동 감지 시: WebSocket으로 구독자에게 푸시
```

#### API 엔드포인트
```
GET    /api/v1/prices/{partId}/current     # 현재 가격 (판매처별)
GET    /api/v1/prices/{partId}/history     # 가격 이력
POST   /api/v1/prices/alerts               # 가격 알림 등록
DELETE /api/v1/prices/alerts/{id}          # 가격 알림 삭제
GET    /api/v1/prices/alerts/my            # 내 알림 목록
GET    /api/v1/prices/deals                # 오늘의 특가
```

### 4.4 사용자 시스템 (User System)

#### 인증 (UNI_match JWT + OAuth 구조 기반)
- **회원가입**: 이메일 인증 (Spring Mail)
- **로그인**: JWT Access/Refresh Token
- **소셜 로그인**: 카카오, 네이버, 구글 (OAuth 2.0)
- **역할**: `USER`, `SELLER`, `ADMIN`

#### API 엔드포인트
```
POST   /api/v1/auth/signup                 # 회원가입
POST   /api/v1/auth/login                  # 로그인
POST   /api/v1/auth/refresh                # 토큰 갱신
POST   /api/v1/auth/oauth/{provider}       # 소셜 로그인
GET    /api/v1/users/me                    # 내 정보
PUT    /api/v1/users/me                    # 정보 수정
GET    /api/v1/users/me/builds             # 내 견적 목록
GET    /api/v1/users/me/favorites          # 찜 목록
GET    /api/v1/users/me/alerts             # 알림 목록
```

### 4.5 커뮤니티 (Community)

#### 기능
- **견적 상담 게시판**: 질문/답변
- **부품 리뷰**: 별점 + 텍스트 + 사진 리뷰
- **견적 자랑 게시판**: 완성된 PC 공유
- **뉴스/정보**: 신제품 소식, 할인 정보

#### API 엔드포인트
```
# 게시판
GET    /api/v1/posts                       # 게시글 목록
POST   /api/v1/posts                       # 게시글 작성
GET    /api/v1/posts/{id}                  # 게시글 상세
PUT    /api/v1/posts/{id}                  # 게시글 수정
DELETE /api/v1/posts/{id}                  # 게시글 삭제

# 댓글
POST   /api/v1/posts/{id}/comments         # 댓글 작성
PUT    /api/v1/comments/{id}               # 댓글 수정
DELETE /api/v1/comments/{id}               # 댓글 삭제

# 리뷰
POST   /api/v1/parts/{id}/reviews          # 부품 리뷰 작성
GET    /api/v1/parts/{id}/reviews          # 부품 리뷰 목록
```

### 4.6 판매자 시스템 (Seller System)

#### 기능
- **입점 신청**: 사업자 인증 후 판매자 등록
- **상품 가격 등록/수정**: 판매 가격 직접 관리
- **판매 통계**: 클릭수, 전환율 대시보드

#### API 엔드포인트
```
POST   /api/v1/sellers/register            # 판매자 등록 신청
GET    /api/v1/sellers/me/products         # 내 상품 목록
PUT    /api/v1/sellers/products/{id}/price # 가격 수정
GET    /api/v1/sellers/me/stats            # 판매 통계
```

### 4.7 관리자 시스템 (Admin)

#### 기능
- **부품 데이터 관리**: CRUD + 일괄 업로드 (CSV/Excel)
- **사용자 관리**: 정지, 역할 변경
- **판매자 승인**: 입점 심사
- **크롤링 모니터링**: 크롤러 상태, 실패 로그
- **통계 대시보드**: 트래픽, 인기 부품, 매출

#### API 엔드포인트
```
# 부품 관리
POST   /api/v1/admin/parts                 # 부품 등록
PUT    /api/v1/admin/parts/{id}            # 부품 수정
DELETE /api/v1/admin/parts/{id}            # 부품 삭제
POST   /api/v1/admin/parts/bulk-upload     # 일괄 업로드

# 사용자/판매자 관리
GET    /api/v1/admin/users                 # 사용자 목록
PUT    /api/v1/admin/users/{id}/status     # 사용자 상태 변경
GET    /api/v1/admin/sellers/pending       # 입점 심사 대기
PUT    /api/v1/admin/sellers/{id}/approve  # 입점 승인/거절

# 모니터링
GET    /api/v1/admin/crawlers/status       # 크롤러 상태
GET    /api/v1/admin/stats/dashboard       # 대시보드 통계
```

---

## 5. 데이터베이스 설계 (ERD 개요)

### 5.1 핵심 테이블

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│    users     │     │      builds      │     │    parts     │
├──────────────┤     ├──────────────────┤     ├──────────────┤
│ id (PK)      │     │ id (PK)          │     │ id (PK)      │
│ email        │◄────│ user_id (FK)     │     │ category     │
│ password     │     │ title            │  ┌─▶│ name         │
│ nickname     │     │ description      │  │  │ manufacturer │
│ role         │     │ total_price      │  │  │ specs (JSON) │
│ social_type  │     │ is_public        │  │  │ image_url    │
│ status       │     │ view_count       │  │  │ created_at   │
│ created_at   │     │ created_at       │  │  └──────────────┘
└──────────────┘     └──────────────────┘  │
                            │              │
                     ┌──────┴───────┐      │
                     │ build_parts  │      │
                     ├──────────────┤      │
                     │ id (PK)      │      │
                     │ build_id(FK) │      │
                     │ part_id (FK) │──────┘
                     │ quantity     │
                     └──────────────┘

┌────────────────┐     ┌──────────────────┐
│    sellers     │     │  price_entries   │
├────────────────┤     ├──────────────────┤
│ id (PK)        │     │ id (PK)          │
│ user_id (FK)   │◄────│ seller_id (FK)   │
│ business_name  │     │ part_id (FK)     │──▶ parts
│ business_num   │     │ price            │
│ status         │     │ url              │
│ approved_at    │     │ in_stock         │
└────────────────┘     │ updated_at       │
                       └──────────────────┘

┌────────────────┐     ┌──────────────────┐
│ price_history  │     │  price_alerts    │
├────────────────┤     ├──────────────────┤
│ id (PK)        │     │ id (PK)          │
│ part_id (FK)   │     │ user_id (FK)     │
│ seller_id (FK) │     │ part_id (FK)     │
│ price          │     │ target_price     │
│ recorded_at    │     │ is_active        │
└────────────────┘     │ created_at       │
                       └──────────────────┘

┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│    posts     │     │    comments      │     │   reviews    │
├──────────────┤     ├──────────────────┤     ├──────────────┤
│ id (PK)      │     │ id (PK)          │     │ id (PK)      │
│ user_id (FK) │     │ post_id (FK)     │     │ user_id (FK) │
│ board_type   │     │ user_id (FK)     │     │ part_id (FK) │
│ title        │     │ content          │     │ rating       │
│ content      │     │ created_at       │     │ content      │
│ view_count   │     └──────────────────┘     │ images       │
│ created_at   │                              │ created_at   │
└──────────────┘                              └──────────────┘

┌──────────────────┐     ┌──────────────────┐
│   favorites      │     │  notifications   │
├──────────────────┤     ├──────────────────┤
│ id (PK)          │     │ id (PK)          │
│ user_id (FK)     │     │ user_id (FK)     │
│ part_id (FK)     │     │ type             │
│ created_at       │     │ title            │
└──────────────────┘     │ message          │
                         │ is_read          │
                         │ created_at       │
                         └──────────────────┘
```

### 5.2 부품 스펙 저장 전략
부품별 스펙이 모두 다르므로 **하이브리드 방식** 채택:
- **공통 필드**: `parts` 테이블 컬럼 (name, manufacturer, category, price 등)
- **카테고리별 스펙**: `specs` 컬럼에 **JSON** 형태로 저장 (MySQL 8.0 JSON 지원)
- **검색 최적화**: Elasticsearch에 비정규화된 인덱스 생성

---

## 6. Docker 배포 구성 (UNI_match 기반 확장)

### 6.1 Dockerfile (멀티스테이지 빌드)
UNI_match-Server의 Dockerfile 구조를 그대로 계승:
```dockerfile
# Stage 1: Build (eclipse-temurin:23-jdk)
# - Gradle 래퍼/설정 복사 → 의존성 캐시 → 소스 빌드
# Stage 2: Runtime (eclipse-temurin:23-jre)
# - bootJar 복사 → JVM 컨테이너 최적화 옵션으로 실행
# - UseContainerSupport, MaxRAMPercentage=75.0
```

### 6.2 Docker Compose 구성
UNI_match의 MySQL + Redis + App 구조를 확장:

```yaml
services:
  # ── 데이터베이스 ──
  mysql:           # MySQL 8.0 (메인 DB)
  redis:           # Redis 7 Alpine (캐시)
  elasticsearch:   # Elasticsearch 8.x (검색 엔진) ← 추가

  # ── 애플리케이션 ──
  app:             # Spring Boot (지우컴 백엔드)
  frontend:        # Next.js (프론트엔드) ← 추가

  # ── 인프라 ──
  nginx:           # Nginx (리버스 프록시) ← 추가

volumes:
  mysql-data:
  redis-data:
  es-data:         # Elasticsearch 데이터 ← 추가
  upload-data:

networks:
  jiucom-network:
    driver: bridge
```

### 6.3 환경 분리 (UNI_match의 profile 구조 기반)
```
application.yml          # 공통 설정
application-dev.yml      # 개발 (ddl-auto: create-drop, H2 가능)
application-prod.yml     # 운영 (ddl-auto: validate, Flyway, S3)
application-test.yml     # 테스트 (H2 인메모리)
```

### 6.4 배포 파이프라인
```
GitHub Push → GitHub Actions CI
  ├─ 테스트 실행 (JUnit + JaCoCo)
  ├─ Docker 이미지 빌드
  ├─ Docker Hub / AWS ECR 푸시
  └─ 서버에서 docker-compose pull & up
```

---

## 7. 프로젝트 구조 (패키지)

```
com.jiucom.api
├── domain/
│   ├── part/
│   │   ├── controller/PartController.java
│   │   ├── service/PartService.java
│   │   ├── repository/PartRepository.java
│   │   ├── repository/PartRepositoryCustom.java      # QueryDSL
│   │   ├── repository/PartRepositoryCustomImpl.java
│   │   ├── entity/Part.java
│   │   ├── dto/request/PartSearchRequest.java
│   │   └── dto/response/PartResponse.java
│   │
│   ├── build/
│   │   ├── controller/BuildController.java
│   │   ├── service/BuildService.java
│   │   ├── service/CompatibilityService.java          # 호환성 검증
│   │   ├── repository/BuildRepository.java
│   │   ├── entity/Build.java
│   │   ├── entity/BuildPart.java
│   │   └── dto/...
│   │
│   ├── price/
│   │   ├── controller/PriceController.java
│   │   ├── service/PriceService.java
│   │   ├── service/PriceAlertService.java
│   │   ├── repository/PriceHistoryRepository.java
│   │   ├── entity/PriceEntry.java
│   │   ├── entity/PriceHistory.java
│   │   ├── entity/PriceAlert.java
│   │   └── dto/...
│   │
│   ├── user/
│   │   ├── controller/UserController.java
│   │   ├── controller/AuthController.java
│   │   ├── service/UserService.java
│   │   ├── repository/UserRepository.java
│   │   ├── entity/User.java
│   │   ├── entity/RefreshToken.java
│   │   └── dto/...
│   │
│   ├── seller/
│   │   ├── controller/SellerController.java
│   │   ├── service/SellerService.java
│   │   ├── entity/Seller.java
│   │   └── dto/...
│   │
│   ├── community/
│   │   ├── controller/PostController.java
│   │   ├── controller/CommentController.java
│   │   ├── service/PostService.java
│   │   ├── service/ReviewService.java
│   │   ├── entity/Post.java
│   │   ├── entity/Comment.java
│   │   ├── entity/Review.java
│   │   └── dto/...
│   │
│   ├── notification/
│   │   ├── controller/NotificationController.java
│   │   ├── service/NotificationService.java
│   │   └── entity/Notification.java
│   │
│   ├── favorite/
│   │   ├── controller/FavoriteController.java
│   │   ├── service/FavoriteService.java
│   │   └── entity/Favorite.java
│   │
│   └── admin/
│       ├── controller/AdminController.java
│       ├── service/AdminService.java
│       └── dto/...
│
├── global/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── RedisConfig.java
│   │   ├── QuerydslConfig.java
│   │   ├── SwaggerConfig.java
│   │   ├── WebSocketConfig.java
│   │   ├── WebMvcConfig.java
│   │   ├── ElasticsearchConfig.java
│   │   └── BatchConfig.java
│   │
│   ├── jwt/
│   │   ├── JwtTokenProvider.java
│   │   └── JwtAuthenticationFilter.java
│   │
│   ├── exception/
│   │   ├── GlobalException.java
│   │   ├── ExceptionAdvice.java
│   │   └── ErrorCode.java
│   │
│   ├── response/
│   │   └── ApiResponse.java
│   │
│   ├── entity/
│   │   └── BaseTimeEntity.java
│   │
│   ├── util/
│   │   └── RedisUtil.java
│   │
│   └── scheduler/
│       ├── PriceCrawlScheduler.java
│       └── NotificationCleanupScheduler.java
│
├── crawler/
│   ├── CrawlerService.java
│   ├── CoupangCrawler.java
│   ├── CompuzoneCrawler.java
│   └── ...
│
└── JiucomApplication.java
```

---

## 8. 개발 로드맵

### Phase 1: MVP (4주)
| 주차 | 백엔드 | 프론트엔드 |
|------|--------|-----------|
| 1주 | 프로젝트 셋업, DB 설계, Docker 구성 | 프로젝트 셋업, 디자인 시스템 |
| 2주 | 유저 인증 (JWT+OAuth), 부품 CRUD | 로그인/회원가입, 부품 목록 페이지 |
| 3주 | 견적 시스템, 호환성 검증 | 견적 빌더 UI, 호환성 표시 |
| 4주 | 가격 비교 기본, 크롤러 1차 | 가격 비교 페이지, 검색 |

### Phase 2: 핵심 기능 완성 (4주)
| 주차 | 백엔드 | 프론트엔드 |
|------|--------|-----------|
| 5주 | Elasticsearch 연동, 고급 필터 | 상세 필터 UI, 자동완성 |
| 6주 | 가격 이력/추이, 최저가 알림 (WebSocket) | 가격 차트, 알림 설정 |
| 7주 | 커뮤니티 (게시판, 리뷰) | 커뮤니티 페이지 |
| 8주 | 판매자 시스템, 관리자 기본 | 판매자 대시보드, 관리자 페이지 |

### Phase 3: 고도화 (4주)
| 주차 | 내용 |
|------|------|
| 9주 | 추천 견적 AI, 성능 최적화 |
| 10주 | SEO 최적화 (SSR), 모바일 반응형 완성 |
| 11주 | 모니터링 (Actuator + Grafana), 부하 테스트 |
| 12주 | QA, 버그 픽스, 프로덕션 배포 |

---

## 9. 비기능 요구사항

### 9.1 성능
- 부품 목록 API 응답: **200ms 이내** (Redis 캐시 적용)
- 검색 응답: **300ms 이내** (Elasticsearch)
- 동시 접속: **1,000명** 이상 지원
- 가격 크롤링: **1시간 주기** 갱신

### 9.2 보안 (UNI_match 보안 구조 계승)
- JWT + Refresh Token 인증
- Spring Security CORS 설정
- Rate Limiting (인터셉터)
- 입력 값 Validation (Bean Validation)
- SQL Injection 방지 (JPA Parameterized Query)
- XSS 방지 (입출력 Sanitization)

### 9.3 모니터링
- Spring Actuator (`/health`, `/info`, `/metrics`)
- 구조적 로깅 (Logback → JSON)
- 크롤러 실패 알림

### 9.4 테스트
- **단위 테스트**: Service 계층 (Mockito)
- **통합 테스트**: Repository + Controller (H2 + SpringBootTest)
- **커버리지**: JaCoCo 70% 이상 목표
- **API 문서**: Swagger UI 자동 생성

---

## 10. UNI_match-Server에서 재사용 가능한 구성요소

| 구성요소 | 파일/패키지 | 재사용 방식 |
|----------|------------|------------|
| JWT 인증 | `jwt/JwtTokenProvider`, `JwtAuthenticationFilter` | 거의 그대로 이식 |
| Security 설정 | `config/SecurityConfig` | 역할(ROLE) 수정 후 이식 |
| Redis 유틸 | `util/RedisUtil`, `config/RedisConfig` | 그대로 이식 |
| API 응답 표준 | `response/ApiResponse`, `ResponseDto` | 그대로 이식 |
| 예외 처리 | `exception/ExceptionAdvice`, `GlobalException` | 그대로 이식 |
| Base Entity | `entity/BaseTimeEntity` | 그대로 이식 |
| QueryDSL 설정 | `config/QuerydslConfig` | 그대로 이식 |
| Swagger 설정 | `config/SwaggerConfig` | 그대로 이식 |
| WebSocket 설정 | `config/WebSocketConfig` | 알림 시스템용으로 수정 |
| Dockerfile | `Dockerfile` | 프로젝트명만 수정 |
| Docker Compose | `docker-compose.yml` | ES, Nginx 추가 확장 |
| Rate Limiter | `interceptor/RateLimitInterceptor` | 그대로 이식 |
| File Uploader | `file/FileUploader`, `FileValidator` | 이미지 업로드용 이식 |
| 알림 스케줄러 | `scheduler/NotificationCleanupScheduler` | 패턴 참고 |
| Flyway 구조 | `resources/db/migration` | 마이그레이션 구조 참고 |
| 프로필 분리 | `application-{dev,prod,test}.yml` | 환경 분리 패턴 동일 |

---

## 11. 참고 사항

### 11.1 다나와 대비 차별화 포인트
1. **호환성 자동 검증** - 부품 선택 시 실시간으로 비호환 부품 필터링
2. **견적 소셜 기능** - 견적 공유/복제/좋아요 생태계
3. **깔끔한 모던 UI** - 다나와 대비 세련된 디자인
4. **모바일 퍼스트** - 반응형 웹으로 모바일 사용성 극대화

### 11.2 법적 고려사항
- 가격 크롤링 시 robots.txt 준수
- 이미지 저작권 (제조사 공식 이미지 사용)
- 개인정보처리방침 수립 필요
- 전자상거래법 준수 (가격 표시 기준)

---

> **다음 단계**: 이 기획서를 승인하면, Phase 1 MVP부터 개발을 시작합니다.
> 백엔드는 UNI_match-Server의 코드를 기반으로 빠르게 구축할 수 있습니다.
