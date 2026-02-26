-- Dev Seed Data for JIUCOM
-- NOTE: This runs only in dev profile with H2 (create-drop)

-- Admin User (password: admin123 - BCrypt encoded)
-- Use signup API for testing; this is a bootstrap admin account
INSERT INTO users (email, password, nickname, role, social_type, status, is_deleted, created_at, updated_at)
VALUES ('admin@jiucom.com', '$2a$10$snyPNY7H6Kx1YNJ3pJsPnunZIJSvBzTTuMWYTr9fVgFU.1sSun/Je', 'admin', 'ADMIN', 'LOCAL', 'ACTIVE', false, NOW(), NOW());

-- Sample Sellers
INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('다나와', 'https://www.danawa.com', null, 'ACTIVE', 4.5, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('컴퓨존', 'https://www.compuzone.co.kr', null, 'ACTIVE', 4.2, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('11번가', 'https://www.11st.co.kr', null, 'ACTIVE', 4.0, false, NOW(), NOW());

-- Sample Parts (Week 2: GPU, CPU, RAM)
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NVIDIA GeForce RTX 4070 Ti SUPER', 'GPU', 'NVIDIA', 'RTX4070TISUPER', null,
        '{"vram":"16GB GDDR6X","coreClock":"2340MHz","tdp":"285W","interface":"PCIe 4.0 x16"}',
        1190000, 1390000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('AMD Ryzen 7 7800X3D', 'CPU', 'AMD', '7800X3D', null,
        '{"cores":8,"threads":16,"baseClock":"4.2GHz","boostClock":"5.0GHz","tdp":"120W","socket":"AM5"}',
        449000, 520000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Samsung DDR5 32GB (16GBx2)', 'RAM', 'Samsung', 'M323R2GA3BB0', null,
        '{"capacity":"32GB (16GBx2)","speed":"DDR5-5600","latency":"CL40","voltage":"1.1V"}',
        139000, 169000, false, NOW(), NOW());

-- Week 3 추가 부품: MOTHERBOARD, POWER_SUPPLY (호환성 테스트용)
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('ASUS ROG STRIX B650E-F GAMING WIFI', 'MOTHERBOARD', 'ASUS', 'B650E-F', null,
        '{"socket":"AM5","chipset":"B650E","formFactor":"ATX","memorySlots":4,"maxMemory":"128GB","m2Slots":3}',
        289000, 339000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Corsair RM850x 850W 80+ Gold', 'POWER_SUPPLY', 'Corsair', 'RM850x', null,
        '{"wattage":"850W","efficiency":"80+ Gold","modular":"Full","fan":"135mm"}',
        159000, 189000, false, NOW(), NOW());

-- Price Entries (Part 1: GPU - 3 sellers)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (1, 1, 1190000, 'https://www.danawa.com/product/rtx4070tisuper', true, false, NOW(), NOW());

INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (1, 2, 1250000, 'https://www.compuzone.co.kr/product/rtx4070tisuper', true, false, NOW(), NOW());

INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (1, 3, 1390000, 'https://www.11st.co.kr/product/rtx4070tisuper', true, false, NOW(), NOW());

-- Price Entries (Part 2: CPU - 2 sellers)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (2, 1, 449000, 'https://www.danawa.com/product/7800x3d', true, false, NOW(), NOW());

INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (2, 2, 470000, 'https://www.compuzone.co.kr/product/7800x3d', true, false, NOW(), NOW());

-- Price Entries (Part 3: RAM - 2 sellers)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (3, 1, 139000, 'https://www.danawa.com/product/ddr5-32gb', true, false, NOW(), NOW());

INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (3, 3, 155000, 'https://www.11st.co.kr/product/ddr5-32gb', true, false, NOW(), NOW());

-- Price Entries (Part 4: MOTHERBOARD - 2 sellers)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (4, 1, 289000, 'https://www.danawa.com/product/b650e-f', true, false, NOW(), NOW());

INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (4, 2, 310000, 'https://www.compuzone.co.kr/product/b650e-f', true, false, NOW(), NOW());

-- Price Entries (Part 5: PSU - 2 sellers)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (5, 1, 159000, 'https://www.danawa.com/product/rm850x', true, false, NOW(), NOW());

INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at)
VALUES (5, 3, 179000, 'https://www.11st.co.kr/product/rm850x', true, false, NOW(), NOW());

-- Sample Build (공개 견적 - admin 사용자, 5개 부품)
INSERT INTO builds (user_id, name, description, total_price, is_public, view_count, like_count, is_deleted, created_at, updated_at)
VALUES (1, '2025 게이밍 견적', 'AM5 플랫폼 기반 고성능 게이밍 PC', 2226000, true, 15, 3, false, NOW(), NOW());

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at)
VALUES (1, 1, 1, 1190000, false, NOW(), NOW());

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at)
VALUES (1, 2, 1, 449000, false, NOW(), NOW());

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at)
VALUES (1, 3, 1, 139000, false, NOW(), NOW());

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at)
VALUES (1, 4, 1, 289000, false, NOW(), NOW());

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at)
VALUES (1, 5, 1, 159000, false, NOW(), NOW());

-- Price History (30일간 GPU/CPU 가격 변동)
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (1, 1, 1250000, DATEADD('DAY', -30, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (1, 1, 1230000, DATEADD('DAY', -25, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (1, 1, 1210000, DATEADD('DAY', -20, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (1, 1, 1200000, DATEADD('DAY', -15, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (1, 1, 1190000, DATEADD('DAY', -10, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (1, 1, 1190000, DATEADD('DAY', -5, CURRENT_DATE), false, NOW(), NOW());

INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (2, 1, 480000, DATEADD('DAY', -30, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (2, 1, 465000, DATEADD('DAY', -20, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (2, 1, 455000, DATEADD('DAY', -10, CURRENT_DATE), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at)
VALUES (2, 1, 449000, CURRENT_DATE, false, NOW(), NOW());

-- Week 4: Sample Posts (FREE, QNA, NEWS)
INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (1, 'FREE', '첫 게이밍 PC 조립 후기', '7800X3D + RTX 4070 Ti Super 조합으로 조립했습니다. 성능이 정말 만족스럽네요!', 42, 5, 2, false, NOW(), NOW());

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (1, 'QNA', 'DDR5 램 호환성 질문', 'B650E 메인보드에 DDR5-5600 램 사용하면 XMP 프로필 잘 잡히나요?', 28, 1, 1, false, NOW(), NOW());

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (1, 'NEWS', 'RTX 5070 출시 임박', 'NVIDIA에서 RTX 5070 시리즈를 2025년 하반기 출시 예정이라고 발표했습니다.', 156, 12, 0, false, NOW(), NOW());

-- Week 4: Sample Comments (post 1에 댓글 2개, 1개는 대댓글)
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 1, null, '조립 과정이 궁금합니다! 자세한 후기 부탁드려요.', 2, false, NOW(), NOW());

INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 1, 1, '네, 다음에 조립 과정 사진과 함께 올리겠습니다!', 1, false, NOW(), NOW());

INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 1, null, 'XMP 잘 잡힙니다. BIOS에서 EXPO 프로필 활성화하세요.', 3, false, NOW(), NOW());

-- Week 4: Sample Reviews (GPU, CPU에 대한 admin 리뷰)
INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 1, 5, 'RTX 4070 Ti Super는 1440p 게이밍에 최적입니다. 발열도 양호하고 소음도 적어요.', 8, false, NOW(), NOW());

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 2, 5, '7800X3D는 게이밍 최강 CPU입니다. 3D V-Cache 덕분에 프레임이 확 올라갑니다.', 12, false, NOW(), NOW());

-- Week 4: Sample Favorites (admin -> GPU, RAM)
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at)
VALUES (1, 1, false, NOW(), NOW());

INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at)
VALUES (1, 3, false, NOW(), NOW());

-- Phase 2: Sample Likes (admin -> post 1, review 1, build 1)
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at)
VALUES (1, 'POST', 1, false, NOW(), NOW());

INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at)
VALUES (1, 'REVIEW', 1, false, NOW(), NOW());

INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at)
VALUES (1, 'BUILD', 1, false, NOW(), NOW());
