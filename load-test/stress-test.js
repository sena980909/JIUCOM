import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate } from 'k6/metrics';

// Stress Test: 시스템 한계 탐색 (점진적 부하 증가)
// 실행: k6 run load-test/stress-test.js

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const errorRate = new Rate('errors');

export const options = {
    stages: [
        { duration: '2m', target: 50 },    // 준비 운동
        { duration: '3m', target: 100 },   // 정상 부하
        { duration: '3m', target: 200 },   // 높은 부하
        { duration: '3m', target: 300 },   // 스트레스 부하
        { duration: '3m', target: 500 },   // 극한 부하
        { duration: '2m', target: 0 },     // 복구
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'],  // 스트레스에서도 2초 이내
        http_req_failed: ['rate<0.15'],     // 15% 미만 실패
    },
};

export default function () {
    group('Read Heavy Workload', function () {
        // 부품 검색 (가장 빈번한 요청)
        const parts = http.get(`${BASE_URL}/parts?page=0&size=20`);
        check(parts, { 'parts ok': (r) => r.status === 200 }) || errorRate.add(1);

        // 가격 비교
        const partId = Math.floor(Math.random() * 5) + 1;
        const prices = http.get(`${BASE_URL}/prices/compare?partId=${partId}`);
        check(prices, { 'prices ok': (r) => r.status === 200 || r.status === 404 });

        // 게시글 목록
        const posts = http.get(`${BASE_URL}/posts?page=0&size=10`);
        check(posts, { 'posts ok': (r) => r.status === 200 }) || errorRate.add(1);

        // 통합 검색
        const keywords = ['RTX', 'i7', 'SSD', 'DDR5', 'AMD', 'RAM', 'CPU', 'GPU'];
        const keyword = keywords[Math.floor(Math.random() * keywords.length)];
        const search = http.get(`${BASE_URL}/search?keyword=${keyword}&page=0&size=10`);
        check(search, { 'search ok': (r) => r.status === 200 }) || errorRate.add(1);
    });

    sleep(Math.random() * 1 + 0.2);
}
