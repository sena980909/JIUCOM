import http from 'k6/http';
import { check, sleep } from 'k6';

// Smoke Test: 최소 부하로 시스템 정상 동작 확인
// 실행: k6 run load-test/smoke-test.js

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

export const options = {
    vus: 1,
    duration: '30s',
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.15'],  // Rate Limiter(429) 허용
    },
};

export default function () {
    // 1. Health Check
    const health = http.get(`${BASE_URL}/actuator/health`);
    check(health, {
        'health status 200': (r) => r.status === 200,
    });

    // 2. 부품 목록 조회
    const parts = http.get(`${BASE_URL}/parts?page=0&size=10`);
    check(parts, {
        'parts ok (200 or 429)': (r) => r.status === 200 || r.status === 429,
    });

    // 3. 게시글 목록 조회
    const posts = http.get(`${BASE_URL}/posts?page=0&size=10`);
    check(posts, {
        'posts ok (200 or 429)': (r) => r.status === 200 || r.status === 429,
    });

    // 4. 통합 검색
    const search = http.get(`${BASE_URL}/search?keyword=RTX&page=0&size=10`);
    check(search, {
        'search ok (200 or 429)': (r) => r.status === 200 || r.status === 429,
    });

    // 5. 견적 목록 조회
    const builds = http.get(`${BASE_URL}/builds?page=0&size=10`);
    check(builds, {
        'builds ok (200 or 429)': (r) => r.status === 200 || r.status === 429,
    });

    sleep(1);
}
