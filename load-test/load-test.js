import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Load Test: 일반 부하 시나리오 (100 동시 사용자)
// 실행: k6 run load-test/load-test.js
// JSON 결과: k6 run --out json=load-test/results/load-test-result.json load-test/load-test.js

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

// Custom Metrics
const loginDuration = new Trend('login_duration', true);
const searchDuration = new Trend('search_duration', true);
const partDetailDuration = new Trend('part_detail_duration', true);
const errorRate = new Rate('errors');

export const options = {
    stages: [
        { duration: '1m', target: 20 },   // Ramp-up: 0→20 VUs
        { duration: '3m', target: 50 },   // Steady: 50 VUs
        { duration: '2m', target: 100 },  // Peak: 100 VUs
        { duration: '3m', target: 100 },  // Sustain peak
        { duration: '1m', target: 0 },    // Ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500', 'p(99)<1000'],
        http_req_failed: ['rate<0.05'],
        errors: ['rate<0.1'],
        login_duration: ['p(95)<800'],
        search_duration: ['p(95)<300'],
        part_detail_duration: ['p(95)<200'],
    },
};

// 테스트용 사용자 풀
const TEST_USERS = Array.from({ length: 100 }, (_, i) => ({
    email: `loadtest${i}@test.com`,
    password: 'password123',
    nickname: `loaduser${i}`,
}));

export function setup() {
    // 테스트 사용자 생성 (회원가입)
    const tokens = [];
    for (let i = 0; i < 10; i++) {
        const res = http.post(`${BASE_URL}/auth/signup`, JSON.stringify(TEST_USERS[i]), {
            headers: { 'Content-Type': 'application/json' },
        });
        if (res.status === 201 && res.json().data) {
            tokens.push(res.json().data.accessToken);
        }
    }
    return { tokens };
}

export default function (data) {
    const vuId = __VU % 10;
    const token = data.tokens[vuId] || null;
    const authHeaders = token
        ? { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
        : { headers: { 'Content-Type': 'application/json' } };

    // 시나리오 1: 비회원 브라우징 (60%)
    if (Math.random() < 0.6) {
        group('비회원 브라우징', function () {
            // 부품 목록 조회
            const parts = http.get(`${BASE_URL}/parts?page=0&size=20`);
            check(parts, { 'parts 200': (r) => r.status === 200 }) || errorRate.add(1);

            sleep(0.5);

            // 부품 상세 조회 (ID 1~5 랜덤)
            const partId = Math.floor(Math.random() * 5) + 1;
            const detail = http.get(`${BASE_URL}/parts/${partId}`);
            partDetailDuration.add(detail.timings.duration);
            check(detail, { 'part detail ok': (r) => r.status === 200 || r.status === 404 });

            sleep(0.3);

            // 통합 검색
            const keywords = ['RTX', 'i7', 'SSD', 'DDR5', 'AMD'];
            const keyword = keywords[Math.floor(Math.random() * keywords.length)];
            const search = http.get(`${BASE_URL}/search?keyword=${keyword}&page=0&size=10`);
            searchDuration.add(search.timings.duration);
            check(search, { 'search ok': (r) => r.status === 200 }) || errorRate.add(1);

            sleep(0.5);

            // 게시글 목록
            const posts = http.get(`${BASE_URL}/posts?page=0&size=10`);
            check(posts, { 'posts ok': (r) => r.status === 200 }) || errorRate.add(1);
        });
    }

    // 시나리오 2: 회원 활동 (30%)
    if (Math.random() < 0.3 && token) {
        group('회원 활동', function () {
            // 프로필 조회
            const profile = http.get(`${BASE_URL}/users/me`, authHeaders);
            check(profile, { 'profile ok': (r) => r.status === 200 });

            sleep(0.3);

            // 견적 목록 조회
            const builds = http.get(`${BASE_URL}/builds/my?page=0&size=10`, authHeaders);
            check(builds, { 'my builds ok': (r) => r.status === 200 || r.status === 403 });

            sleep(0.3);

            // 알림 조회
            const notifs = http.get(`${BASE_URL}/notifications?page=0&size=10`, authHeaders);
            check(notifs, { 'notifications ok': (r) => r.status === 200 });

            // 즐겨찾기 조회
            const favs = http.get(`${BASE_URL}/favorites?page=0&size=10`, authHeaders);
            check(favs, { 'favorites ok': (r) => r.status === 200 });
        });
    }

    // 시나리오 3: 로그인/토큰 갱신 (10%)
    if (Math.random() < 0.1) {
        group('인증 플로우', function () {
            const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
                email: TEST_USERS[vuId].email,
                password: TEST_USERS[vuId].password,
            }), { headers: { 'Content-Type': 'application/json' } });

            loginDuration.add(loginRes.timings.duration);
            check(loginRes, { 'login ok': (r) => r.status === 200 }) || errorRate.add(1);
        });
    }

    sleep(Math.random() * 2 + 0.5);
}
