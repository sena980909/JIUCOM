#!/bin/bash
# 백엔드 Docker Compose 배포 스크립트
set -e

PROJECT_DIR="/home/ec2-user/jiucom"
ENV_FILE="${PROJECT_DIR}/.env.aws"

echo "===== JIUCOM 백엔드 배포 시작 ====="

# .env 파일 확인
if [ ! -f "$ENV_FILE" ]; then
    echo "❌ .env.aws 파일이 없습니다. 먼저 생성하세요."
    exit 1
fi

cd "$PROJECT_DIR"

# 최신 코드 가져오기
echo "[1/4] 최신 코드 가져오기..."
git pull origin master

# 기존 컨테이너 중지
echo "[2/4] 기존 컨테이너 중지..."
docker-compose -f docker-compose.aws.yml --env-file .env.aws down || true

# 이미지 빌드 및 시작
echo "[3/4] 이미지 빌드 및 컨테이너 시작..."
docker-compose -f docker-compose.aws.yml --env-file .env.aws up -d --build

# 헬스체크 대기
echo "[4/4] 헬스체크 대기 (최대 60초)..."
for i in $(seq 1 12); do
    if curl -sf http://localhost:8080/api/v1/actuator/health > /dev/null 2>&1; then
        echo "✅ API 서버 정상 구동!"
        break
    fi
    if [ $i -eq 12 ]; then
        echo "⚠️  타임아웃: API 서버 응답 없음. 로그를 확인하세요:"
        echo "   docker-compose -f docker-compose.aws.yml logs app"
        exit 1
    fi
    echo "   대기중... ($i/12)"
    sleep 5
done

echo ""
echo "===== 배포 완료 ====="
echo "API: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'YOUR-EC2-IP'):80/api/v1/swagger-ui.html"
docker-compose -f docker-compose.aws.yml ps
