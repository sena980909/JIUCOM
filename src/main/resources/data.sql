-- Dev Seed Data for JIUCOM
-- NOTE: This runs only in dev profile with H2 (create-drop)

-- Admin User (password: admin123 - BCrypt encoded)
-- Use signup API for testing; this is a bootstrap admin account
INSERT INTO users (email, password, nickname, role, social_type, status, is_deleted, created_at, updated_at)
VALUES ('admin@jiucom.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6CQARPiU12hB6JaH/SqEZ76Kq', 'admin', 'ADMIN', 'LOCAL', 'ACTIVE', false, NOW(), NOW());

-- Sample Sellers
INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('다나와', 'https://www.danawa.com', null, 'ACTIVE', 4.5, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('컴퓨존', 'https://www.compuzone.co.kr', null, 'ACTIVE', 4.2, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('11번가', 'https://www.11st.co.kr', null, 'ACTIVE', 4.0, false, NOW(), NOW());

-- Sample Parts
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
