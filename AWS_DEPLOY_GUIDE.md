# JIUCOM AWS 배포 가이드

AWS 프리티어를 활용한 JIUCOM 배포 매뉴얼입니다.

## 아키텍처

```
[브라우저] → CloudFront (HTTPS) → S3 (React 정적 파일)
    │
    └→ EC2 t3.micro (Docker Compose)
           ├── Nginx (:80, 리버스 프록시)
           ├── Spring Boot API (:8080)
           └── Redis 7 (캐시)
                 │
                 └→ RDS db.t3.micro (MySQL 8.0)
```

## 비용 (프리티어 12개월)

| 리소스 | 스펙 | 월 비용 |
|--------|------|--------|
| EC2 | t3.micro (1vCPU, 1GB) | $0 |
| RDS | db.t3.micro (20GB) | $0 |
| S3 | 정적 파일 | $0 |
| CloudFront | CDN 1TB/월 | $0 |
| EBS | 20GB gp3 | $0 |

---

## Step 1: 보안 그룹 생성

### EC2 보안 그룹
```bash
aws ec2 create-security-group \
  --group-name jiucom-ec2-sg \
  --description "JIUCOM EC2 Security Group"

# SSH
aws ec2 authorize-security-group-ingress \
  --group-name jiucom-ec2-sg \
  --protocol tcp --port 22 --cidr 0.0.0.0/0

# HTTP
aws ec2 authorize-security-group-ingress \
  --group-name jiucom-ec2-sg \
  --protocol tcp --port 80 --cidr 0.0.0.0/0

# HTTPS
aws ec2 authorize-security-group-ingress \
  --group-name jiucom-ec2-sg \
  --protocol tcp --port 443 --cidr 0.0.0.0/0
```

### RDS 보안 그룹
```bash
EC2_SG_ID=$(aws ec2 describe-security-groups \
  --group-names jiucom-ec2-sg \
  --query 'SecurityGroups[0].GroupId' --output text)

aws ec2 create-security-group \
  --group-name jiucom-rds-sg \
  --description "JIUCOM RDS Security Group"

# MySQL (EC2에서만 접근)
aws ec2 authorize-security-group-ingress \
  --group-name jiucom-rds-sg \
  --protocol tcp --port 3306 \
  --source-group $EC2_SG_ID
```

---

## Step 2: RDS MySQL 생성

```bash
aws rds create-db-instance \
  --db-instance-identifier jiucom-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0 \
  --master-username admin \
  --master-user-password <YOUR-DB-PASSWORD> \
  --allocated-storage 20 \
  --storage-type gp2 \
  --db-name jiucom_db \
  --vpc-security-group-ids <RDS-SG-ID> \
  --backup-retention-period 7 \
  --no-multi-az \
  --no-publicly-accessible
```

RDS 엔드포인트 확인:
```bash
aws rds describe-db-instances \
  --db-instance-identifier jiucom-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

---

## Step 3: EC2 인스턴스 생성

### 키페어 생성
```bash
aws ec2 create-key-pair \
  --key-name jiucom-key \
  --query 'KeyMaterial' \
  --output text > jiucom-key.pem

chmod 400 jiucom-key.pem
```

### 인스턴스 생성
```bash
# Amazon Linux 2023 AMI (리전에 따라 다름)
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t3.micro \
  --key-name jiucom-key \
  --security-group-ids <EC2-SG-ID> \
  --block-device-mappings '[{"DeviceName":"/dev/xvda","Ebs":{"VolumeSize":20,"VolumeType":"gp3"}}]' \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=jiucom-server}]'
```

### 탄력적 IP 연결
```bash
ALLOC_ID=$(aws ec2 allocate-address --query 'AllocationId' --output text)
INSTANCE_ID=$(aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=jiucom-server" "Name=instance-state-name,Values=running" \
  --query 'Reservations[0].Instances[0].InstanceId' --output text)

aws ec2 associate-address --allocation-id $ALLOC_ID --instance-id $INSTANCE_ID
```

---

## Step 4: EC2 초기 설정

```bash
# SSH 접속
ssh -i jiucom-key.pem ec2-user@<EC2-PUBLIC-IP>

# 초기 설정 스크립트 (또는 수동 실행)
# Docker, Docker Compose, Git 설치 + 프로젝트 클론
curl -fsSL https://raw.githubusercontent.com/your-username/jiucom/master/scripts/setup-ec2.sh | bash

# Docker 그룹 적용
newgrp docker
```

---

## Step 5: 백엔드 배포

```bash
cd /home/ec2-user/jiucom

# 환경변수 설정
cp .env.aws.example .env.aws
nano .env.aws
# DB_HOST=<RDS-ENDPOINT>
# DB_PASSWORD=<YOUR-DB-PASSWORD>
# JWT_SECRET=<RANDOM-64-CHARS>
# CORS_ORIGINS=https://<CLOUDFRONT-DOMAIN>

# 배포
bash scripts/deploy-backend.sh

# 확인
curl http://localhost/api/v1/actuator/health
# {"status":"UP"}
```

---

## Step 6: S3 버킷 생성 + 프론트 업로드

```bash
# S3 버킷 생성
aws s3 mb s3://jiucom-frontend

# 정적 웹사이트 호스팅 설정
aws s3 website s3://jiucom-frontend \
  --index-document index.html \
  --error-document index.html

# 퍼블릭 읽기 정책
aws s3api put-bucket-policy --bucket jiucom-frontend --policy '{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "PublicReadGetObject",
    "Effect": "Allow",
    "Principal": "*",
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::jiucom-frontend/*"
  }]
}'

# 프론트엔드 빌드 & 업로드 (로컬에서)
cd frontend
echo "VITE_API_BASE_URL=http://<EC2-PUBLIC-IP>/api/v1" > .env.production
npm run build
aws s3 sync dist/ s3://jiucom-frontend --delete
```

---

## Step 7: CloudFront 배포

```bash
aws cloudfront create-distribution \
  --origin-domain-name jiucom-frontend.s3.amazonaws.com \
  --default-root-object index.html \
  --query 'Distribution.DomainName' \
  --output text
```

### SPA 대응: 커스텀 에러 페이지
CloudFront 콘솔에서:
- Error Pages → Create Custom Error Response
- HTTP Error Code: 403 → Response Page Path: /index.html → HTTP Response Code: 200
- HTTP Error Code: 404 → Response Page Path: /index.html → HTTP Response Code: 200

---

## Step 8: CORS/OAuth 업데이트

CloudFront 도메인 확인 후:

```bash
# EC2에서 .env.aws 수정
ssh -i jiucom-key.pem ec2-user@<EC2-IP>
cd /home/ec2-user/jiucom

# CORS 업데이트
sed -i 's|CORS_ORIGINS=.*|CORS_ORIGINS=https://<CLOUDFRONT-DOMAIN>|' .env.aws

# 재배포
bash scripts/deploy-backend.sh
```

프론트엔드도 API URL 업데이트:
```bash
# 로컬에서
echo "VITE_API_BASE_URL=http://<EC2-IP>/api/v1" > frontend/.env.production
bash scripts/deploy-frontend-s3.sh jiucom-frontend <CLOUDFRONT-DIST-ID>
```

---

## 검증 체크리스트

- [ ] EC2 Health: `curl http://<EC2-IP>/api/v1/actuator/health`
- [ ] RDS 연결: `docker-compose -f docker-compose.aws.yml logs app | grep Flyway`
- [ ] Redis: `docker exec jiucom-redis redis-cli ping`
- [ ] S3: `https://jiucom-frontend.s3.amazonaws.com/index.html`
- [ ] CloudFront: `https://<cloudfront-domain>`
- [ ] API 통신: 프론트에서 로그인 테스트
- [ ] SPA 라우팅: `/parts`, `/builds` 등 직접 접근 시 정상 표시

---

## 유용한 명령어

```bash
# 로그 확인
docker-compose -f docker-compose.aws.yml logs -f app
docker-compose -f docker-compose.aws.yml logs -f nginx

# 컨테이너 상태
docker-compose -f docker-compose.aws.yml ps

# 재시작
docker-compose -f docker-compose.aws.yml restart app

# 전체 중지
docker-compose -f docker-compose.aws.yml down

# 디스크 정리
docker system prune -a -f
```

---

## 문제 해결

### 메모리 부족 (t3.micro 1GB)
```bash
# 스왑 파일 생성 (2GB)
sudo dd if=/dev/zero of=/swapfile bs=128M count=16
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
```

### Gradle 빌드 OOM
Dockerfile에서 빌드하므로 EC2 메모리와 무관. Docker BuildKit 사용 시:
```bash
DOCKER_BUILDKIT=1 docker-compose -f docker-compose.aws.yml build
```

### RDS 연결 실패
- 보안 그룹: RDS-SG에 EC2-SG로부터 3306 인바운드 허용 확인
- 같은 VPC 내에 있는지 확인
- RDS가 `available` 상태인지 확인
