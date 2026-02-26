# JIUCOM Phase 3 - 포트폴리오 완성도 강화

## 현재까지 완료된 것

| Phase | 내용 | 상태 |
|-------|------|------|
| Phase 1 | 프로젝트 부트스트랩, JWT 인증, 부품/견적/가격, 커뮤니티, 알림, Admin | 완료 |
| Phase 2 | 테스트 96개, 좋아요/검색/이미지/이메일, 보안/모니터링, CI/CD | 완료 |
| MSA 인프라 | Docker 9서비스, Nginx Gateway, Prometheus+Grafana, Zipkin 분산추적 | 완료 |

**현재 수치**: Java 파일 150+, API 70+, 테스트 96개, Docker 서비스 9개

---

## 다음에 할 수 있는 것들 (우선순위순)

---

### 1. 프론트엔드 구현 (React/Next.js)

**포트폴리오 임팩트: 매우 높음**

현재 백엔드 API만 있어서 시각적으로 보여줄 수 있는 화면이 없음.
면접관/채용담당자에게 실제 동작하는 서비스를 보여주면 차원이 다른 임팩트.

**기술 스택 제안:**
- Next.js 14 (App Router) + TypeScript
- Tailwind CSS + shadcn/ui (빠른 UI 구축)
- Zustand 또는 Tanstack Query (상태 관리)
- Docker Compose에 frontend 서비스 추가 (총 10개)

**구현 범위 (핵심 페이지만):**

| 페이지 | 주요 기능 | 연동 API |
|--------|----------|---------|
| 메인 페이지 | 인기 부품, 최근 게시글, 추천 견적 | GET /parts, /posts, /builds |
| 로그인/회원가입 | JWT 인증 폼 | POST /auth/login, /auth/signup |
| 부품 목록 | 카테고리 필터, 정렬, 검색 | GET /parts, /search |
| 부품 상세 | 스펙, 가격비교 차트, 리뷰 | GET /parts/{id}, /prices/compare |
| 견적 만들기 | 부품 선택, 호환성 표시, 총 가격 | POST /builds, GET /parts |
| 커뮤니티 | 게시판, 댓글, 좋아요 | GET/POST /posts, /comments, /likes |
| 마이페이지 | 내 견적, 찜목록, 알림 | GET /users/me, /builds/my, /favorites |

**예상 기간**: 2~3주
**난이도**: 중

---

### 2. 소셜 로그인 (OAuth2)

**포트폴리오 임팩트: 높음**

카카오/네이버/구글 소셜 로그인은 실무에서 거의 필수이고,
면접에서 "OAuth2 플로우 설명해보세요" 질문에 직접 구현 경험으로 답할 수 있음.

**구현 내용:**
- Spring Security OAuth2 Client 설정
- 카카오, 네이버, 구글 3사 연동
- 기존 JWT 발급 체계와 통합 (소셜 로그인 → JWT 토큰 발급)
- User 엔티티에 provider, providerId 필드 추가
- 소셜 계정 연동/해제 API

**신규 파일:**
- `OAuth2SuccessHandler.java` - 소셜 로그인 성공 후 JWT 발급
- `CustomOAuth2UserService.java` - 사용자 정보 처리
- `OAuth2UserInfo.java` - 카카오/네이버/구글 응답 파싱

**예상 기간**: 3~4일
**난이도**: 중

---

### 3. Kubernetes 배포 설정 (k8s)

**포트폴리오 임팩트: 높음 (인프라/DevOps 어필)**

현재 Docker Compose → Kubernetes manifest로 전환.
실제 배포 안 해도 k8s yaml 파일만 있으면 "쿠버네티스 설계 경험" 어필 가능.

**구현 내용:**
```
k8s/
├── namespace.yml
├── app/
│   ├── deployment.yml        # Spring Boot (replicas: 2)
│   ├── service.yml           # ClusterIP
│   └── hpa.yml               # 오토스케일링 (CPU 70%)
├── mysql/
│   ├── statefulset.yml       # StatefulSet + PVC
│   ├── service.yml           # Headless Service
│   └── secret.yml            # DB 비밀번호
├── redis/
│   ├── deployment.yml
│   └── service.yml
├── nginx/
│   ├── deployment.yml
│   ├── service.yml           # LoadBalancer (외부 진입점)
│   └── configmap.yml         # nginx.conf
├── monitoring/
│   ├── prometheus-deployment.yml
│   ├── grafana-deployment.yml
│   └── prometheus-configmap.yml
└── ingress.yml               # Ingress Controller 설정
```

**핵심 포인트:**
- Deployment vs StatefulSet 사용 분리 (MySQL은 StatefulSet)
- ConfigMap/Secret으로 설정 분리
- HPA(Horizontal Pod Autoscaler)로 오토스케일링
- Liveness/Readiness Probe 설정
- Resource requests/limits

**예상 기간**: 3~4일
**난이도**: 중상

---

### 4. 메시지 큐 도입 (Kafka 또는 RabbitMQ)

**포트폴리오 임팩트: 높음 (MSA 핵심 패턴)**

현재 동기 처리 중인 것들을 비동기 이벤트 기반으로 전환.
MSA에서 서비스 간 느슨한 결합(loose coupling)의 핵심.

**적용 대상:**
- 가격 알림 → 이벤트 발행 → 이메일/푸시 소비자
- 댓글 알림 → 이벤트 발행 → 알림 소비자
- 가격 크롤링 결과 → 이벤트 → 가격 기록 + 알림 체크

**구현 내용:**
- Docker Compose에 Kafka (또는 RabbitMQ) 추가
- `PriceAlertEvent`, `CommentNotificationEvent` 이벤트 클래스
- `@KafkaListener` 또는 `@RabbitListener` 소비자
- Spring ApplicationEvent로 도메인 이벤트 발행

**예상 기간**: 3~5일
**난이도**: 중상

---

### 5. 성능 최적화 + 부하 테스트

**포트폴리오 임팩트: 중상 (실무 감각 어필)**

"성능을 측정하고 개선한 경험"은 면접에서 강력한 무기.

**구현 내용:**

1) **Redis 캐시 전략 고도화**
   - 부품 목록 캐싱 (TTL 10분)
   - 인기 검색어 캐싱
   - Cache Aside 패턴 문서화

2) **쿼리 최적화**
   - N+1 문제 점검 + Fetch Join 적용
   - 슬로우 쿼리 식별 + 인덱스 추가
   - QueryDSL Projection으로 필요한 컬럼만 조회

3) **부하 테스트 (k6 또는 JMeter)**
   - 시나리오: 부품 검색 → 상세 → 견적 생성
   - 목표: 동시 100명, 평균 응답 200ms 이하
   - 결과 리포트 + Grafana 대시보드 스크린샷

**예상 기간**: 3~4일
**난이도**: 중

---

### 6. API 버전 관리 + Swagger 고도화

**포트폴리오 임팩트: 중**

**구현 내용:**
- API 버전 전략 문서화 (URI vs Header)
- Swagger 응답 예시 추가 (@ExampleObject)
- 에러 응답 스키마 통일 + 문서화
- Postman Collection 내보내기

**예상 기간**: 1~2일
**난이도**: 하

---

### 7. Elasticsearch 검색 엔진

**포트폴리오 임팩트: 중상**

현재 QueryDSL LIKE 검색 → Elasticsearch 전문 검색으로 전환.

**구현 내용:**
- Docker Compose에 Elasticsearch 7 추가
- 부품 인덱스 매핑 (한글 형태소 분석기: nori)
- Spring Data Elasticsearch 연동
- 검색 자동완성 (Edge N-gram)
- 가격 범위 필터 (Range Query)

**예상 기간**: 4~5일
**난이도**: 중상

---

### 8. 테스트 커버리지 확대

**포트폴리오 임팩트: 중**

현재 96개 → 150개 이상으로 확대.

**추가 대상:**
- Controller 슬라이스 테스트 보강 (Build, Price, Review, Favorite)
- Repository 테스트 (@DataJpaTest + QueryDSL)
- 예외 케이스 테스트 (인증 실패, 권한 부족, 유효성 검증)
- Security 관련 테스트 (미인증 접근 403, Rate Limit 429)

**예상 기간**: 2~3일
**난이도**: 하~중

---

## 추천 우선순위

### A. 풀스택 개발자 지원이라면

```
1순위: 프론트엔드 (React/Next.js)  ← 눈에 보이는 결과물
2순위: 소셜 로그인 (OAuth2)        ← 실무 필수 기능
3순위: 성능 최적화 + 부하 테스트    ← 면접 소재
```

### B. 백엔드 개발자 지원이라면

```
1순위: 메시지 큐 (Kafka/RabbitMQ)  ← MSA 핵심 패턴
2순위: Kubernetes 배포 설정        ← 인프라 설계 경험
3순위: 소셜 로그인 (OAuth2)        ← 실무 필수
4순위: 성능 최적화 + 부하 테스트    ← 면접 소재
```

### C. 인프라/DevOps 어필이라면

```
1순위: Kubernetes 배포 설정        ← k8s 설계 경험
2순위: 메시지 큐 (Kafka)           ← 이벤트 기반 아키텍처
3순위: Elasticsearch              ← 분산 시스템 경험
4순위: 부하 테스트 + 성능 튜닝     ← 운영 감각
```

---

## 현재 프로젝트의 강점 (면접 어필 포인트)

이미 갖추고 있는 것들:

- **70+ REST API** - 실무급 규모의 API 설계 경험
- **96개 테스트** - 단위 + 통합 테스트 전략 수립 경험
- **Docker 9서비스** - MSA 지향 아키텍처 설계 + 오케스트레이션
- **Prometheus + Grafana** - 관측성(Observability) 스택 구축 경험
- **Zipkin 분산 추적** - 마이크로서비스 디버깅 기법 이해
- **Nginx API Gateway** - API Gateway 패턴 구현 경험
- **JWT + Spring Security** - 인증/인가 구현 경험
- **QueryDSL** - 타입 안전한 동적 쿼리 구현 경험
- **GitHub Actions CI/CD** - 자동화 빌드/테스트 파이프라인
- **Flyway** - DB 마이그레이션 버전 관리

부족한 것들 (위 계획으로 보완 가능):

- 프론트엔드 (시각적 결과물)
- 소셜 로그인 (OAuth2)
- 메시지 큐 (비동기 이벤트 처리)
- Kubernetes (컨테이너 오케스트레이션)
- 부하 테스트 결과 (성능 수치)
