#!/bin/bash
# 프론트엔드 빌드 → S3 업로드 스크립트
set -e

S3_BUCKET="${1:-jiucom-frontend}"
CLOUDFRONT_DIST_ID="${2:-}"
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

echo "===== JIUCOM 프론트엔드 배포 시작 ====="

# Node.js 확인
if ! command -v node &> /dev/null; then
    echo "❌ Node.js가 설치되어 있지 않습니다."
    exit 1
fi

# 프론트엔드 빌드
echo "[1/3] 프론트엔드 빌드..."
cd "${PROJECT_DIR}/frontend"
npm install
npm run build

# S3 업로드
echo "[2/3] S3 업로드 (s3://${S3_BUCKET})..."
aws s3 sync dist/ "s3://${S3_BUCKET}" \
    --delete \
    --cache-control "public, max-age=31536000" \
    --exclude "index.html" \
    --exclude "*.json"

# index.html은 캐시 짧게
aws s3 cp dist/index.html "s3://${S3_BUCKET}/index.html" \
    --cache-control "public, max-age=0, must-revalidate" \
    --content-type "text/html"

# CloudFront 캐시 무효화
if [ -n "$CLOUDFRONT_DIST_ID" ]; then
    echo "[3/3] CloudFront 캐시 무효화..."
    aws cloudfront create-invalidation \
        --distribution-id "$CLOUDFRONT_DIST_ID" \
        --paths "/*"
else
    echo "[3/3] CloudFront 배포 ID 미지정 - 캐시 무효화 건너뜀"
fi

echo ""
echo "===== 프론트엔드 배포 완료 ====="
echo "S3: https://${S3_BUCKET}.s3.amazonaws.com/index.html"
