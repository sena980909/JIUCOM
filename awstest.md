# JIUCOM AWS 성능/클라우드 모니터링 결과

**측정일시**: 2026-02-27 21:14 KST
**측정자**: 자동 수집 (CloudWatch + EC2 SSH + cURL)

---

## 1. 인프라 개요

| 항목 | 값 |
|------|-----|
| EC2 Instance ID | i-033eb104d2946dcc7 |
| Instance Name | jiucom-server |
| Instance Type | t3.micro (2 vCPU, 1GB RAM) |
| Region | ap-northeast-2 (서울) |
| OS | Amazon Linux 2023 |
| Public IP | 43.200.200.69 |
| Uptime | 10시간 10분 |
| CloudFront | dbbbjuenom6zb.cloudfront.net (E3SUNW1I4FKFCM) |
| S3 Bucket | jiucom-frontend-app |
| RDS | jiucom-db.c3yw4koe8hr2.ap-northeast-2.rds.amazonaws.com (MySQL 8.0) |

---

## 2. EC2 시스템 리소스

### 2-1. 메모리

| 항목 | 값 |
|------|-----|
| Total | 911 MiB |
| Used | 655 MiB (71.9%) |
| Free | 63 MiB |
| Available | 128 MiB |
| Swap Total | 2.0 GiB |
| Swap Used | 219 MiB |

### 2-2. 디스크

| 파티션 | 전체 | 사용 | 여유 | 사용률 |
|--------|------|------|------|--------|
| /dev/nvme0n1p1 | 20 GB | 4.8 GB | 16 GB | 24% |

### 2-3. Load Average

```
0.00, 0.00, 0.00
```

---

## 3. Docker 컨테이너 상태

| Container | Memory | Mem % | CPU % | Net I/O | Block I/O |
|-----------|--------|-------|-------|---------|-----------|
| jiucom-api | 378.6 MiB / 450 MiB | 84.12% | 0.22% | 120KB / 92.7KB | 125MB / 22.3MB |
| jiucom-redis | 5.4 MiB / 96 MiB | 5.63% | 2.92% | 5.84KB / 14KB | 20MB / 1.46MB |
| jiucom-nginx | 1.3 MiB / 64 MiB | 2.07% | 0.00% | 3.83MB / 3.88MB | 40.7MB / 3.93MB |

### Docker 이미지

| Repository | Tag | Size |
|------------|-----|------|
| jiucom-app | latest | 352 MB |
| redis | 7-alpine | 41.4 MB |
| nginx | alpine | 62.1 MB |

---

## 4. CloudWatch 메트릭 (최근 24시간)

### 4-1. CPU 사용률 (시간별)

| 시간 (KST) | 평균 (%) | 최대 (%) | 비고 |
|-------------|---------|---------|------|
| 11:59 | 11.32 | 82.46 | 배포 작업 |
| 12:59 | 23.45 | 83.36 | 배포 작업 |
| 13:59 | 5.76 | 73.93 | 배포 작업 |
| 14:59 | 6.27 | 73.19 | 배포 작업 |
| 15:59 | 1.10 | 1.24 | 안정 |
| 16:59 | 1.08 | 1.22 | 안정 |
| 17:59 | 1.12 | 1.91 | 안정 |
| 18:59 | 1.15 | 1.83 | 안정 |
| 19:59 | 9.59 | 80.87 | 배포 작업 |
| 20:59 | 2.83 | 26.54 | 안정화 |

- **평상시 CPU**: ~1% (매우 여유)
- **배포 시 CPU**: 최대 83% (Docker 빌드 + Spring Boot 컴파일)

### 4-2. CPU 크레딧 잔량

| 시간 (KST) | 크레딧 잔량 |
|-------------|------------|
| 16:14 | 1.03 (위험) |
| 17:14 | 10.06 |
| 18:14 | 20.74 |
| 19:14 | 30.55 |
| 20:14 | 33.85 |
| 21:14 | 40.67 (회복) |

- t3.micro 최대 크레딧: 144
- 배포 작업 시 크레딧 급감 (1.03까지 하락)
- 유휴 상태에서 시간당 ~12 크레딧 회복

### 4-3. 네트워크 트래픽 (시간별)

| 시간 (KST) | In (MB) | Out (MB) | 비고 |
|-------------|---------|----------|------|
| 12:00 | 1,291.7 | 5.4 | 배포 (코드 업로드) |
| 13:00 | 1,080.1 | 7.7 | 배포 (Docker pull) |
| 14:00 | 282.8 | 4.5 | 배포 작업 |
| 15:00 | 243.3 | 1.9 | 배포 작업 |
| 16:00 | 0.08 | 0.09 | 유휴 |
| 17:00 | 0.07 | 0.09 | 유휴 |
| 18:00 | 0.45 | 0.42 | 유휴 |
| 19:00 | 0.81 | 2.97 | 접속 |
| 20:00 | 458.3 | 1.72 | 배포 |
| 21:00 | 0.57 | 0.48 | 유휴 |

### 4-4. 상태 체크

| 시간대 | 실패 횟수 |
|--------|----------|
| 최근 24시간 전체 | **0건** (정상) |

---

## 5. 응답 속도 측정

### 5-1. 프론트엔드 (CloudFront)

| 측정 항목 | 값 |
|-----------|-----|
| URL | https://dbbbjuenom6zb.cloudfront.net |
| HTTP Status | 200 OK |
| DNS Lookup | 27ms |
| TCP Connect | 53ms |
| TTFB (Time to First Byte) | 127ms |
| Total Time | 127ms |
| Download Size | 2,420 bytes |

### 5-2. 백엔드 API (Health Check)

| 측정 항목 | 값 |
|-----------|-----|
| URL | http://43.200.200.69/api/v1/actuator/health |
| HTTP Status | 200 OK |
| DNS Lookup | <1ms |
| TCP Connect | 12ms |
| TTFB (Time to First Byte) | 41ms |
| Total Time | 44ms |
| Response | `{"status":"UP"}` |

---

## 6. 서비스 상태 요약

| 서비스 | 상태 | 비고 |
|--------|------|------|
| Spring Boot API | UP | 정상 구동 |
| Redis | Healthy | 캐시 정상 |
| Nginx | Running | 리버스 프록시 정상 |
| MySQL (RDS) | Connected | Flyway 마이그레이션 완료, Schema up to date |
| CloudFront | Deployed | 캐시 무효화 완료 |
| S3 | Active | 프론트엔드 정적 파일 서빙 |

---

## 7. JVM 설정

| 항목 | 값 |
|------|-----|
| Java | Eclipse Temurin 17 (JRE) |
| MaxRAMPercentage | 55% (of 450MB = ~248MB heap) |
| InitialRAMPercentage | 35% (of 450MB = ~158MB heap) |
| GC | G1GC + StringDeduplication |
| Actual Memory Usage | 378.6 MiB |

---

## 8. 비용 현황

| 항목 | 값 |
|------|-----|
| 요금 플랜 | AWS Free Tier |
| 남은 크레딧 | $135.31 USD |
| 크레딧 만료 | 2026-06-08 (101일 남음) |
| EC2 (t3.micro) | 프리티어 750시간/월 |
| RDS | 프리티어 범위 내 |
| S3 | 프리티어 범위 내 |
| CloudFront | 프리티어 범위 내 |

---

## 9. 개선 권장사항

### 즉시 (현재 인프라)
- [x] Docker dangling 이미지 정리 (6.2GB 절감 완료)
- [x] Docker builder cache 정리 (7GB 절감 완료)
- [x] JVM 메모리 최적화 (G1GC, StringDeduplication 적용)
- [ ] Redis maxmemory를 48MB로 축소 완료

### 향후 (트래픽 증가 시)
- t3.small(2GB RAM) 업그레이드 고려 (~$15/월)
- RDS 인스턴스 스펙 확인 및 필요 시 조정
- CloudFront 캐시 TTL 최적화 (정적 자산 1년, index.html 즉시 갱신)
- ELB + Auto Scaling 도입 검토
