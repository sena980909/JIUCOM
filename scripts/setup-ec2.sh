#!/bin/bash
# EC2 초기 설정 스크립트 (Amazon Linux 2023)
set -e

echo "===== JIUCOM EC2 초기 설정 시작 ====="

# 시스템 업데이트
echo "[1/5] 시스템 업데이트..."
sudo dnf update -y

# Docker 설치
echo "[2/5] Docker 설치..."
sudo dnf install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

# Docker Compose 설치
echo "[3/5] Docker Compose 설치..."
DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep '"tag_name"' | cut -d'"' -f4)
sudo curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version

# Git 설치
echo "[4/5] Git 설치..."
sudo dnf install -y git

# 프로젝트 클론
echo "[5/5] 프로젝트 클론..."
if [ ! -d "/home/ec2-user/jiucom" ]; then
    cd /home/ec2-user
    git clone https://github.com/your-username/jiucom.git
    cd jiucom
else
    cd /home/ec2-user/jiucom
    git pull origin master
fi

# .env 파일 확인
if [ ! -f "/home/ec2-user/jiucom/.env.aws" ]; then
    echo ""
    echo "⚠️  .env.aws 파일이 없습니다!"
    echo "   .env.aws.example을 참고하여 .env.aws를 생성하세요:"
    echo "   cp .env.aws.example .env.aws"
    echo "   nano .env.aws"
fi

echo ""
echo "===== 초기 설정 완료 ====="
echo "다음 단계: newgrp docker 실행 후 deploy-backend.sh 실행"
