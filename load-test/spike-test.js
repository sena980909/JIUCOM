import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Spike Test: 순간 트래픽 급증 시나리오 (이벤트/프로모션)
// 실행: k6 run load-test/spike-test.js

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const errorRate = new Rate('errors');

export const options = {
    stages: [
        { duration: '30s', target: 10 },   // 평상시
        { duration: '10s', target: 500 },   // 스파이크!
        { duration: '1m', target: 500 },    // 스파이크 유지
        { duration: '10s', target: 10 },    // 급감
        { duration: '1m', target: 10 },     // 복구 관찰
        { duration: '10s', target: 0 },     // 종료
    ],
    thresholds: {
        http_req_duration: ['p(95)<3000'],  // 스파이크에서도 3초 이내
        http_req_failed: ['rate<0.20'],     // 20% 미만 실패
    },
};

export default function () {
    // 부품 목록 (가장 빈번한 페이지)
    const parts = http.get(`${BASE_URL}/parts?page=0&size=20`);
    check(parts, { 'parts ok': (r) => r.status === 200 }) || errorRate.add(1);

    // 통합 검색
    const search = http.get(`${BASE_URL}/search?keyword=RTX&page=0&size=10`);
    check(search, { 'search ok': (r) => r.status === 200 }) || errorRate.add(1);

    sleep(Math.random() * 0.5);
}
