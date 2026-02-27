-- JIUCOM Production Seed Data
-- 실제 2025년 인기 컴퓨터 부품 데이터

-- =============================================
-- 1. Admin User (password: admin123)
-- =============================================
INSERT INTO users (email, password, nickname, role, social_type, status, is_deleted, created_at, updated_at)
VALUES ('admin@jiucom.com', '$2a$10$snyPNY7H6Kx1YNJ3pJsPnunZIJSvBzTTuMWYTr9fVgFU.1sSun/Je', '관리자', 'ADMIN', 'LOCAL', 'ACTIVE', false, NOW(), NOW());

-- 샘플 유저
INSERT INTO users (email, password, nickname, role, social_type, status, is_deleted, created_at, updated_at)
VALUES ('gamer@jiucom.com', '$2a$10$snyPNY7H6Kx1YNJ3pJsPnunZIJSvBzTTuMWYTr9fVgFU.1sSun/Je', '게이머김', 'USER', 'LOCAL', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT INTO users (email, password, nickname, role, social_type, status, is_deleted, created_at, updated_at)
VALUES ('builder@jiucom.com', '$2a$10$snyPNY7H6Kx1YNJ3pJsPnunZIJSvBzTTuMWYTr9fVgFU.1sSun/Je', '견적마스터', 'USER', 'LOCAL', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO users (email, password, nickname, role, social_type, status, is_deleted, created_at, updated_at)
VALUES ('techie@jiucom.com', '$2a$10$snyPNY7H6Kx1YNJ3pJsPnunZIJSvBzTTuMWYTr9fVgFU.1sSun/Je', '테크매니아', 'USER', 'LOCAL', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =============================================
-- 2. 판매처 (5개)
-- =============================================
INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('다나와', 'https://www.danawa.com', null, 'ACTIVE', 4.8, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('컴퓨존', 'https://www.compuzone.co.kr', null, 'ACTIVE', 4.5, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('11번가', 'https://www.11st.co.kr', null, 'ACTIVE', 4.2, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('쿠팡', 'https://www.coupang.com', null, 'ACTIVE', 4.3, false, NOW(), NOW());

INSERT INTO sellers (name, site_url, logo_url, status, reliability_score, is_deleted, created_at, updated_at)
VALUES ('인터파크', 'https://www.interpark.com', null, 'ACTIVE', 4.0, false, NOW(), NOW());

-- =============================================
-- 3. 부품 (카테고리별 총 30개)
-- =============================================

-- ===== CPU (5개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('AMD Ryzen 7 7800X3D', 'CPU', 'AMD', '7800X3D', null,
        '{"cores":8,"threads":16,"baseClock":"4.2GHz","boostClock":"5.0GHz","tdp":"120W","socket":"AM5","cache":"96MB (L3)","process":"5nm"}',
        449000, 520000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('AMD Ryzen 9 7950X3D', 'CPU', 'AMD', '7950X3D', null,
        '{"cores":16,"threads":32,"baseClock":"4.2GHz","boostClock":"5.7GHz","tdp":"120W","socket":"AM5","cache":"128MB (L3)","process":"5nm"}',
        689000, 790000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Intel Core i7-14700K', 'CPU', 'Intel', 'i7-14700K', null,
        '{"cores":20,"threads":28,"baseClock":"3.4GHz","boostClock":"5.6GHz","tdp":"125W","socket":"LGA1700","cache":"33MB","process":"Intel 7"}',
        429000, 499000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Intel Core i5-14600K', 'CPU', 'Intel', 'i5-14600K', null,
        '{"cores":14,"threads":20,"baseClock":"3.5GHz","boostClock":"5.3GHz","tdp":"125W","socket":"LGA1700","cache":"24MB","process":"Intel 7"}',
        299000, 359000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('AMD Ryzen 5 7600', 'CPU', 'AMD', '7600', null,
        '{"cores":6,"threads":12,"baseClock":"3.8GHz","boostClock":"5.1GHz","tdp":"65W","socket":"AM5","cache":"32MB (L3)","process":"5nm"}',
        199000, 249000, false, NOW(), NOW());

-- ===== GPU (5개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NVIDIA GeForce RTX 4090', 'GPU', 'NVIDIA', 'RTX4090', null,
        '{"vram":"24GB GDDR6X","coreClock":"2520MHz","tdp":"450W","interface":"PCIe 4.0 x16","cudaCores":16384}',
        2290000, 2790000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NVIDIA GeForce RTX 4070 Ti SUPER', 'GPU', 'NVIDIA', 'RTX4070TiS', null,
        '{"vram":"16GB GDDR6X","coreClock":"2340MHz","tdp":"285W","interface":"PCIe 4.0 x16","cudaCores":8448}',
        1190000, 1390000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NVIDIA GeForce RTX 4060 Ti 8GB', 'GPU', 'NVIDIA', 'RTX4060Ti', null,
        '{"vram":"8GB GDDR6","coreClock":"2310MHz","tdp":"160W","interface":"PCIe 4.0 x8","cudaCores":4352}',
        489000, 569000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('AMD Radeon RX 7900 XTX', 'GPU', 'AMD', 'RX7900XTX', null,
        '{"vram":"24GB GDDR6","coreClock":"2500MHz","tdp":"355W","interface":"PCIe 4.0 x16","streamProcessors":6144}',
        1290000, 1490000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NVIDIA GeForce RTX 4060', 'GPU', 'NVIDIA', 'RTX4060', null,
        '{"vram":"8GB GDDR6","coreClock":"2460MHz","tdp":"115W","interface":"PCIe 4.0 x8","cudaCores":3072}',
        369000, 429000, false, NOW(), NOW());

-- ===== MOTHERBOARD (4개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('ASUS ROG STRIX B650E-F GAMING WIFI', 'MOTHERBOARD', 'ASUS', 'B650E-F', null,
        '{"socket":"AM5","chipset":"B650E","formFactor":"ATX","memorySlots":4,"maxMemory":"128GB","m2Slots":3,"wifi":"WiFi 6E"}',
        289000, 339000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('MSI MAG B760 TOMAHAWK WIFI', 'MOTHERBOARD', 'MSI', 'B760-TOMAHAWK', null,
        '{"socket":"LGA1700","chipset":"B760","formFactor":"ATX","memorySlots":4,"maxMemory":"128GB","m2Slots":2,"wifi":"WiFi 6E"}',
        219000, 259000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('GIGABYTE B650 AORUS ELITE AX', 'MOTHERBOARD', 'GIGABYTE', 'B650-AORUS', null,
        '{"socket":"AM5","chipset":"B650","formFactor":"ATX","memorySlots":4,"maxMemory":"128GB","m2Slots":2,"wifi":"WiFi 6E"}',
        239000, 279000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('ASRock B760M Pro RS/D4', 'MOTHERBOARD', 'ASRock', 'B760M-Pro', null,
        '{"socket":"LGA1700","chipset":"B760","formFactor":"Micro-ATX","memorySlots":4,"maxMemory":"128GB","m2Slots":2,"wifi":"없음"}',
        129000, 159000, false, NOW(), NOW());

-- ===== RAM (3개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Samsung DDR5 32GB (16GBx2) 5600MHz', 'RAM', 'Samsung', 'DDR5-5600-32', null,
        '{"capacity":"32GB (16GBx2)","speed":"DDR5-5600","latency":"CL40","voltage":"1.1V","type":"DDR5"}',
        139000, 169000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('G.SKILL Trident Z5 RGB DDR5 32GB (16GBx2) 6000MHz', 'RAM', 'G.SKILL', 'TZ5-6000-32', null,
        '{"capacity":"32GB (16GBx2)","speed":"DDR5-6000","latency":"CL36","voltage":"1.35V","type":"DDR5","rgb":true}',
        189000, 229000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Samsung DDR5 16GB (8GBx2) 4800MHz', 'RAM', 'Samsung', 'DDR5-4800-16', null,
        '{"capacity":"16GB (8GBx2)","speed":"DDR5-4800","latency":"CL40","voltage":"1.1V","type":"DDR5"}',
        69000, 89000, false, NOW(), NOW());

-- ===== SSD (3개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Samsung 990 PRO 2TB NVMe', 'SSD', 'Samsung', '990PRO-2TB', null,
        '{"capacity":"2TB","interface":"PCIe 4.0 x4 NVMe","readSpeed":"7450MB/s","writeSpeed":"6900MB/s","formFactor":"M.2 2280","nand":"V-NAND TLC"}',
        219000, 269000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Samsung 990 PRO 1TB NVMe', 'SSD', 'Samsung', '990PRO-1TB', null,
        '{"capacity":"1TB","interface":"PCIe 4.0 x4 NVMe","readSpeed":"7450MB/s","writeSpeed":"6900MB/s","formFactor":"M.2 2280","nand":"V-NAND TLC"}',
        129000, 159000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('WD Black SN850X 1TB NVMe', 'SSD', 'Western Digital', 'SN850X-1TB', null,
        '{"capacity":"1TB","interface":"PCIe 4.0 x4 NVMe","readSpeed":"7300MB/s","writeSpeed":"6300MB/s","formFactor":"M.2 2280","nand":"TLC"}',
        119000, 149000, false, NOW(), NOW());

-- ===== PSU (3개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Corsair RM850x 850W 80+ Gold', 'POWER_SUPPLY', 'Corsair', 'RM850x', null,
        '{"wattage":"850W","efficiency":"80+ Gold","modular":"Full Modular","fan":"135mm","warranty":"10년"}',
        159000, 189000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Seasonic FOCUS GX-1000 1000W 80+ Gold', 'POWER_SUPPLY', 'Seasonic', 'FOCUS-GX1000', null,
        '{"wattage":"1000W","efficiency":"80+ Gold","modular":"Full Modular","fan":"120mm","warranty":"10년"}',
        199000, 239000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('마이크로닉스 Classic II 700W 80+ Bronze', 'POWER_SUPPLY', 'Micronics', 'Classic2-700', null,
        '{"wattage":"700W","efficiency":"80+ Bronze","modular":"Non-Modular","fan":"120mm","warranty":"3년"}',
        69000, 85000, false, NOW(), NOW());

-- ===== CASE (3개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NZXT H7 Flow', 'CASE', 'NZXT', 'H7-Flow', null,
        '{"formFactor":"ATX Mid Tower","dimensions":"480x230x505mm","fans":"2x120mm 포함","radiator":"최대 360mm","weight":"8.2kg","color":"Black"}',
        149000, 179000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('Fractal Design North', 'CASE', 'Fractal Design', 'North', null,
        '{"formFactor":"ATX Mid Tower","dimensions":"469x215x469mm","fans":"2x140mm 포함","radiator":"최대 360mm","weight":"9.1kg","color":"Charcoal Black"}',
        179000, 209000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('앱코 SUITMASTER H300 GLASS', 'CASE', 'ABKO', 'H300', null,
        '{"formFactor":"ATX Mid Tower","dimensions":"435x210x480mm","fans":"3x120mm RGB 포함","radiator":"최대 240mm","weight":"5.8kg","color":"Black"}',
        59000, 75000, false, NOW(), NOW());

-- ===== COOLER (2개) =====
INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('NZXT Kraken 360 RGB', 'COOLER', 'NZXT', 'Kraken-360', null,
        '{"type":"AIO 수냉","radiator":"360mm","fans":"3x120mm","socket":"AM5/LGA1700","noise":"최대 33dBA","rpm":"500-1800"}',
        249000, 299000, false, NOW(), NOW());

INSERT INTO parts (name, category, manufacturer, model_number, image_url, specs, lowest_price, highest_price, is_deleted, created_at, updated_at)
VALUES ('DeepCool AK620', 'COOLER', 'DeepCool', 'AK620', null,
        '{"type":"타워형 공냉","heatpipes":6,"fans":"2x120mm","socket":"AM5/LGA1700","noise":"최대 28dBA","height":"160mm"}',
        59000, 75000, false, NOW(), NOW());

-- =============================================
-- 4. 가격 엔트리 (부품별 2~4개 판매처)
-- =============================================

-- CPU: 7800X3D (id=1)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (1, 1, 449000, 'https://danawa.com/p/7800x3d', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (1, 2, 465000, 'https://compuzone.co.kr/p/7800x3d', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (1, 4, 459000, 'https://coupang.com/p/7800x3d', true, false, NOW(), NOW());

-- CPU: 7950X3D (id=2)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (2, 1, 689000, 'https://danawa.com/p/7950x3d', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (2, 2, 719000, 'https://compuzone.co.kr/p/7950x3d', true, false, NOW(), NOW());

-- CPU: i7-14700K (id=3)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (3, 1, 429000, 'https://danawa.com/p/i7-14700k', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (3, 3, 449000, 'https://11st.co.kr/p/i7-14700k', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (3, 4, 439000, 'https://coupang.com/p/i7-14700k', true, false, NOW(), NOW());

-- CPU: i5-14600K (id=4)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (4, 1, 299000, 'https://danawa.com/p/i5-14600k', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (4, 2, 315000, 'https://compuzone.co.kr/p/i5-14600k', true, false, NOW(), NOW());

-- CPU: Ryzen 5 7600 (id=5)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (5, 1, 199000, 'https://danawa.com/p/r5-7600', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (5, 4, 209000, 'https://coupang.com/p/r5-7600', true, false, NOW(), NOW());

-- GPU: RTX 4090 (id=6)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (6, 1, 2290000, 'https://danawa.com/p/rtx4090', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (6, 2, 2490000, 'https://compuzone.co.kr/p/rtx4090', true, false, NOW(), NOW());

-- GPU: RTX 4070 Ti Super (id=7)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (7, 1, 1190000, 'https://danawa.com/p/rtx4070tis', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (7, 2, 1250000, 'https://compuzone.co.kr/p/rtx4070tis', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (7, 3, 1290000, 'https://11st.co.kr/p/rtx4070tis', true, false, NOW(), NOW());

-- GPU: RTX 4060 Ti (id=8)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (8, 1, 489000, 'https://danawa.com/p/rtx4060ti', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (8, 4, 509000, 'https://coupang.com/p/rtx4060ti', true, false, NOW(), NOW());

-- GPU: RX 7900 XTX (id=9)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (9, 1, 1290000, 'https://danawa.com/p/rx7900xtx', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (9, 2, 1350000, 'https://compuzone.co.kr/p/rx7900xtx', true, false, NOW(), NOW());

-- GPU: RTX 4060 (id=10)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (10, 1, 369000, 'https://danawa.com/p/rtx4060', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (10, 3, 389000, 'https://11st.co.kr/p/rtx4060', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (10, 4, 379000, 'https://coupang.com/p/rtx4060', true, false, NOW(), NOW());

-- MB: B650E-F (id=11)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (11, 1, 289000, 'https://danawa.com/p/b650ef', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (11, 2, 310000, 'https://compuzone.co.kr/p/b650ef', true, false, NOW(), NOW());

-- MB: B760 Tomahawk (id=12)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (12, 1, 219000, 'https://danawa.com/p/b760-tomahawk', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (12, 4, 229000, 'https://coupang.com/p/b760-tomahawk', true, false, NOW(), NOW());

-- MB: B650 Aorus (id=13)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (13, 1, 239000, 'https://danawa.com/p/b650-aorus', true, false, NOW(), NOW());

-- MB: B760M Pro (id=14)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (14, 1, 129000, 'https://danawa.com/p/b760m-pro', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (14, 3, 139000, 'https://11st.co.kr/p/b760m-pro', true, false, NOW(), NOW());

-- RAM (id=15,16,17)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (15, 1, 139000, 'https://danawa.com/p/ddr5-32gb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (15, 4, 145000, 'https://coupang.com/p/ddr5-32gb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (16, 1, 189000, 'https://danawa.com/p/tz5-32gb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (16, 2, 199000, 'https://compuzone.co.kr/p/tz5-32gb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (17, 1, 69000, 'https://danawa.com/p/ddr5-16gb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (17, 4, 75000, 'https://coupang.com/p/ddr5-16gb', true, false, NOW(), NOW());

-- SSD (id=18,19,20)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (18, 1, 219000, 'https://danawa.com/p/990pro-2tb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (18, 4, 229000, 'https://coupang.com/p/990pro-2tb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (19, 1, 129000, 'https://danawa.com/p/990pro-1tb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (19, 2, 139000, 'https://compuzone.co.kr/p/990pro-1tb', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (20, 1, 119000, 'https://danawa.com/p/sn850x-1tb', true, false, NOW(), NOW());

-- PSU (id=21,22,23)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (21, 1, 159000, 'https://danawa.com/p/rm850x', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (21, 2, 169000, 'https://compuzone.co.kr/p/rm850x', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (22, 1, 199000, 'https://danawa.com/p/focus-gx1000', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (23, 1, 69000, 'https://danawa.com/p/classic2-700', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (23, 4, 72000, 'https://coupang.com/p/classic2-700', true, false, NOW(), NOW());

-- CASE (id=24,25,26)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (24, 1, 149000, 'https://danawa.com/p/h7-flow', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (25, 1, 179000, 'https://danawa.com/p/north', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (25, 2, 189000, 'https://compuzone.co.kr/p/north', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (26, 1, 59000, 'https://danawa.com/p/h300', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (26, 4, 62000, 'https://coupang.com/p/h300', true, false, NOW(), NOW());

-- COOLER (id=27,28)
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (27, 1, 249000, 'https://danawa.com/p/kraken360', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (28, 1, 59000, 'https://danawa.com/p/ak620', true, false, NOW(), NOW());
INSERT INTO price_entries (part_id, seller_id, price, product_url, is_available, is_deleted, created_at, updated_at) VALUES (28, 4, 65000, 'https://coupang.com/p/ak620', true, false, NOW(), NOW());

-- =============================================
-- 5. 가격 히스토리 (주요 부품 30일 변동)
-- =============================================

-- RTX 4070 Ti Super (id=7) 다나와 가격 변동
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1350000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1320000, DATE_SUB(CURDATE(), INTERVAL 25 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1280000, DATE_SUB(CURDATE(), INTERVAL 20 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1250000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1220000, DATE_SUB(CURDATE(), INTERVAL 10 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1200000, DATE_SUB(CURDATE(), INTERVAL 5 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 1, 1190000, CURDATE(), false, NOW(), NOW());
-- RTX 4070 Ti Super 컴퓨존 가격 변동
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 2, 1390000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 2, 1350000, DATE_SUB(CURDATE(), INTERVAL 20 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 2, 1290000, DATE_SUB(CURDATE(), INTERVAL 10 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (7, 2, 1250000, CURDATE(), false, NOW(), NOW());

-- 7800X3D (id=1) 가격 변동
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (1, 1, 520000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (1, 1, 499000, DATE_SUB(CURDATE(), INTERVAL 20 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (1, 1, 475000, DATE_SUB(CURDATE(), INTERVAL 10 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (1, 1, 449000, CURDATE(), false, NOW(), NOW());

-- Samsung 990 PRO 2TB (id=18) 가격 변동
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (18, 1, 269000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (18, 1, 249000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), false, NOW(), NOW());
INSERT INTO price_history (part_id, seller_id, price, record_date, is_deleted, created_at, updated_at) VALUES (18, 1, 219000, CURDATE(), false, NOW(), NOW());

-- =============================================
-- 6. 샘플 견적 (3개)
-- =============================================

-- 견적 1: 하이엔드 게이밍 PC (admin)
INSERT INTO builds (user_id, name, description, total_price, is_public, view_count, like_count, is_deleted, created_at, updated_at)
VALUES (1, '2025 하이엔드 게이밍 PC', 'RTX 4090 + 7950X3D 울트라 하이엔드 조합. 4K 게이밍 최적화.', 4434000, true, 89, 12, false, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY));

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 2, 1, 689000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 6, 1, 2290000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 11, 1, 289000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 16, 1, 189000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 18, 1, 219000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 22, 1, 199000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 25, 1, 179000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (1, 27, 1, 249000, false, NOW(), NOW());

-- 견적 2: 가성비 게이밍 PC (게이머김)
INSERT INTO builds (user_id, name, description, total_price, is_public, view_count, like_count, is_deleted, created_at, updated_at)
VALUES (2, '150만원 가성비 게이밍', '7800X3D + RTX 4060 Ti 조합. 1440p 게이밍 최적. 가성비 최고!', 1493000, true, 156, 23, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 1, 1, 449000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 8, 1, 489000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 13, 1, 239000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 15, 1, 139000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 20, 1, 119000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 23, 1, 69000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 26, 1, 59000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (2, 28, 1, 59000, false, NOW(), NOW());

-- 견적 3: 인텔 작업/게이밍 겸용 (견적마스터)
INSERT INTO builds (user_id, name, description, total_price, is_public, view_count, like_count, is_deleted, created_at, updated_at)
VALUES (3, '인텔 14세대 작업+게이밍', 'i7-14700K + RTX 4070 Ti Super. 영상편집과 게이밍 모두 가능한 만능 PC.', 2723000, true, 67, 8, false, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 3, 1, 429000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 7, 1, 1190000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 12, 1, 219000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 16, 1, 189000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 18, 1, 219000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 21, 1, 159000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 24, 1, 149000, false, NOW(), NOW());
INSERT INTO build_parts (build_id, part_id, quantity, unit_price, is_deleted, created_at, updated_at) VALUES (3, 27, 1, 249000, false, NOW(), NOW());

-- =============================================
-- 7. 샘플 게시글 (8개)
-- =============================================
INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (1, 'NOTICE', '지우컴 오픈 안내', '안녕하세요! 컴퓨터 부품 가격비교 플랫폼 지우컴이 오픈했습니다.\n\n주요 기능:\n- 부품 가격 비교 (5개 쇼핑몰)\n- PC 견적 만들기 (호환성 자동 검사)\n- 가격 변동 알림\n- 커뮤니티\n\n많은 이용 부탁드립니다!', 312, 28, 3, false, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (2, 'FREE', '첫 게이밍 PC 조립 후기', '7800X3D + RTX 4060 Ti 조합으로 150만원대 견적을 짜서 조립했습니다.\n\n솔직히 처음 조립이라 걱정 많이 했는데, 지우컴에서 호환성 체크해주니까 편했어요.\n\n1440p에서 대부분 게임 100프레임 이상 나오고 매우 만족합니다!\n\n다음엔 RTX 5070 나오면 업그레이드 할 예정입니다.', 187, 15, 4, false, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (3, 'FREE', 'RTX 4090 vs RX 7900 XTX 비교', '두 그래픽카드를 모두 사용해본 후기입니다.\n\n- 4K 게이밍: RTX 4090 승 (DLSS 3가 압도적)\n- 가성비: RX 7900 XTX 승 (100만원 차이)\n- 레이트레이싱: RTX 4090 압승\n- 래스터라이제이션: 비슷비슷\n\n결론: 4K 울트라 세팅이 아니면 RX 7900 XTX가 합리적입니다.', 245, 19, 2, false, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (4, 'QNA', 'DDR5 램 호환성 질문', 'B650E 메인보드에 DDR5-6000 램 사용하면 XMP(EXPO) 프로필 잘 잡히나요?\n\nG.SKILL Trident Z5 구매 예정인데 혹시 호환 문제 있을까요?', 78, 3, 3, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (2, 'QNA', 'SSD 추천 부탁드립니다', '게임용 SSD 1TB 추천해주세요.\n\n삼성 990 PRO vs WD SN850X 중에 고민 중입니다.\n실사용 체감 차이가 있나요?', 92, 5, 2, false, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (1, 'FREE', '2025 하반기 출시 예정 부품 정리', '올해 하반기 기대되는 신제품 정리입니다.\n\n- NVIDIA RTX 5070/5060 시리즈\n- AMD Ryzen 9000X3D\n- Intel Arrow Lake-S Refresh\n- DDR5-8000 램 대중화\n- PCIe 5.0 SSD 가격 하락\n\n어떤 제품이 가장 기대되시나요?', 334, 22, 5, false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (3, 'REVIEW', '앱코 H300 케이스 사용기', '가성비 케이스 찾다가 앱코 H300 구매했습니다.\n\n장점:\n- 5만원대 가격에 RGB 팬 3개 포함\n- 강화유리 사이드패널\n- 깔끔한 디자인\n\n단점:\n- 선정리 공간이 좀 좁음\n- 에어플로우는 평범\n- 소재가 약간 얇음\n\n이 가격대에서는 추천합니다.', 126, 8, 1, false, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO posts (user_id, board_type, title, content, view_count, like_count, comment_count, is_deleted, created_at, updated_at)
VALUES (4, 'FREE', '오늘 7800X3D 최저가 갱신!', '다나와에서 7800X3D가 449,000원까지 내려왔네요!\n\n한달 전만 해도 52만원이었는데... 지금이 매수 타이밍인 것 같습니다.\n\n지우컴 가격 알림 설정해두니까 바로 알려줘서 좋네요 ㅎㅎ', 203, 17, 3, false, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =============================================
-- 8. 샘플 댓글 (20개)
-- =============================================
-- 공지 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 2, null, '오픈 축하합니다! 이런 서비스 필요했어요.', 5, false, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 3, null, '가격 비교 + 견적 기능 편리하네요. 잘 쓰겠습니다!', 3, false, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 1, 1, '감사합니다! 더 좋은 서비스로 보답하겠습니다.', 2, false, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY));

-- 조립 후기 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 3, null, '저도 비슷한 견적으로 조립했는데 만족스럽습니다!', 4, false, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 4, null, '발열은 어떤가요? 쿨러 뭐 쓰셨어요?', 2, false, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 2, 5, 'DeepCool AK620 썼는데 풀로드 75도 정도 나와요. 가성비 좋습니다.', 6, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 1, null, '조립 사진도 올려주시면 좋겠어요!', 1, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 4090 vs 7900 XTX 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (3, 2, null, '좋은 비교 감사합니다. DLSS 3 지원 게임이라면 확실히 4090이 좋겠네요.', 3, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (3, 4, null, '가성비 따지면 7900 XTX가 맞는듯. 100만원 차이는 크죠...', 5, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- DDR5 호환성 질문 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (4, 3, null, 'B650E 메인보드에서 EXPO 프로필 잘 잡힙니다. BIOS에서 한번에 설정 가능해요.', 7, false, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (4, 1, null, 'G.SKILL Trident Z5는 AMD QVL에도 올라와있으니 문제없을 겁니다.', 4, false, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (4, 4, 10, '감사합니다! 바로 주문하겠습니다.', 1, false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- SSD 추천 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (5, 1, null, '게임 로딩 체감 차이는 거의 없습니다. 가격 싼 걸로 사세요.', 8, false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (5, 3, null, '990 PRO가 내구성(TBW)이 더 높아서 장기적으로는 990 PRO 추천합니다.', 4, false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 하반기 신제품 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (6, 2, null, 'RTX 5070이 제일 기대됩니다. 4070 Ti급 성능에 가격은 더 낮다고 하던데...', 6, false, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (6, 3, null, '9000X3D 나오면 게이밍 CPU 판도가 또 바뀔 것 같네요.', 3, false, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (6, 4, null, 'PCIe 5.0 SSD 가격 하락이 가장 기대됩니다. 지금은 너무 비싸요...', 2, false, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 7800X3D 최저가 댓글
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (8, 3, null, '가격 알림 기능 진짜 유용하네요! 저도 설정해놨습니다.', 4, false, DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 12 HOUR));
INSERT INTO comments (post_id, user_id, parent_id, content, like_count, is_deleted, created_at, updated_at)
VALUES (8, 1, null, '이 가격이면 진짜 사야겠네요. 게이밍 CPU 끝판왕인데.', 3, false, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- =============================================
-- 9. 샘플 리뷰 (8개)
-- =============================================
INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 1, 5, '게이밍 CPU 최강. 3D V-Cache 덕분에 모든 게임에서 프레임이 10~20% 향상됩니다.', 15, false, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 7, 5, '1440p 울트라 세팅에서 거의 모든 게임 100fps 이상. DLSS 3 지원 게임에서는 체감이 미쳤습니다.', 12, false, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (3, 8, 4, '1080p 게이밍에 충분합니다. 발열도 적고 소음도 양호. 가격 대비 만족.', 8, false, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (4, 18, 5, '읽기/쓰기 속도가 정말 빠릅니다. 게임 로딩 시간이 확 줄었어요.', 6, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (2, 11, 4, '빌드 퀄리티 좋고 확장성 뛰어남. WiFi 6E 기본 포함이라 편리. 가격이 좀 있지만 가치있음.', 5, false, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (3, 28, 5, '이 가격에 이 성능? 가성비 공냉 쿨러 끝판왕입니다. 7800X3D에 사용 중인데 풀로드 75도.', 10, false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (4, 26, 3, '가격 대비 괜찮은 케이스. RGB 팬 포함이라 가성비 좋음. 다만 선정리 공간이 좀 아쉬움.', 4, false, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO reviews (user_id, part_id, rating, content, like_count, is_deleted, created_at, updated_at)
VALUES (1, 6, 5, 'RTX 4090은 현존 최강 그래픽카드. 4K 울트라에서도 60fps를 가볍게 넘깁니다. 전력 소모만 주의하세요.', 18, false, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =============================================
-- 10. 샘플 찜하기
-- =============================================
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (2, 1, false, NOW(), NOW());
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (2, 7, false, NOW(), NOW());
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (2, 18, false, NOW(), NOW());
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (3, 6, false, NOW(), NOW());
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (3, 1, false, NOW(), NOW());
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (4, 8, false, NOW(), NOW());
INSERT INTO favorites (user_id, part_id, is_deleted, created_at, updated_at) VALUES (4, 28, false, NOW(), NOW());

-- =============================================
-- 11. 샘플 좋아요
-- =============================================
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (2, 'POST', 1, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (3, 'POST', 1, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (4, 'POST', 2, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (1, 'POST', 2, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (2, 'BUILD', 1, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (3, 'BUILD', 2, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (4, 'BUILD', 2, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (1, 'REVIEW', 1, false, NOW(), NOW());
INSERT INTO content_likes (user_id, target_type, target_id, is_deleted, created_at, updated_at) VALUES (2, 'REVIEW', 6, false, NOW(), NOW());
