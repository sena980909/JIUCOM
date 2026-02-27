#!/bin/bash
# 전체 배포 스크립트 (백엔드 + 프론트엔드)
set -e

S3_BUCKET="${1:-jiucom-frontend}"
CLOUDFRONT_DIST_ID="${2:-}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=========================================="
echo "   JIUCOM 전체 배포"
echo "=========================================="
echo ""

# 1. 백엔드 배포
echo "▶ [Step 1] 백엔드 배포"
echo "---"
bash "${SCRIPT_DIR}/deploy-backend.sh"
echo ""

# 2. 프론트엔드 배포
echo "▶ [Step 2] 프론트엔드 배포"
echo "---"
bash "${SCRIPT_DIR}/deploy-frontend-s3.sh" "$S3_BUCKET" "$CLOUDFRONT_DIST_ID"
echo ""

echo "=========================================="
echo "   전체 배포 완료!"
echo "=========================================="
EC2_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'YOUR-EC2-IP')
echo "  API:  http://${EC2_IP}/api/v1/swagger-ui.html"
echo "  Web:  https://${S3_BUCKET}.s3.amazonaws.com"
echo "=========================================="
