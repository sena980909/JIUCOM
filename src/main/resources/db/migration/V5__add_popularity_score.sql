-- V5: Add popularity_score column for mainstream-first sorting
ALTER TABLE parts ADD COLUMN popularity_score INT DEFAULT 0;

-- ==============================================
-- Scoring Rules:
--   Base: mainstream price range → 50pts
--         high-end → 40pts, budget → 30pts
--         too cheap/old → 10pts
--   Brand bonus: known good brands → +20pts
--   Generation bonus: current gen → +10pts
-- ==============================================

-- ---- CPU ----
-- Mainstream (150K~400K): Ryzen 5/7, i5/i7 current gen
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='CPU'
  AND lowest_price BETWEEN 150000 AND 400000;
-- High-end (400K~1M)
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='CPU'
  AND lowest_price BETWEEN 400001 AND 1000000;
-- Budget (50K~150K)
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='CPU'
  AND lowest_price BETWEEN 50000 AND 149999;
-- Too cheap (<50K) or very expensive (>1M)
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='CPU'
  AND (lowest_price < 50000 OR lowest_price > 1000000);

-- Brand bonus: AMD Ryzen, Intel Core
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='CPU'
  AND (name LIKE '%라이젠%' OR name LIKE '%Ryzen%' OR name LIKE '%코어%' OR name LIKE '%Core%');
-- Gen bonus: current gen keywords
UPDATE parts SET popularity_score = popularity_score + 10 WHERE is_deleted=0 AND category='CPU'
  AND (name LIKE '%7800X%' OR name LIKE '%7600X%' OR name LIKE '%7900X%' OR name LIKE '%7950X%'
    OR name LIKE '%14600%' OR name LIKE '%14700%' OR name LIKE '%14900%'
    OR name LIKE '%13600%' OR name LIKE '%13700%' OR name LIKE '%13900%'
    OR name LIKE '%9600X%' OR name LIKE '%9700X%' OR name LIKE '%9900X%' OR name LIKE '%9950X%');

-- ---- GPU ----
-- Mainstream (300K~700K): RTX 4060/4070, RX 7600/7700
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='GPU'
  AND lowest_price BETWEEN 300000 AND 700000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='GPU'
  AND lowest_price BETWEEN 700001 AND 1500000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='GPU'
  AND lowest_price BETWEEN 150000 AND 299999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='GPU'
  AND (lowest_price < 150000 OR lowest_price > 1500000);
-- Brand bonus
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='GPU'
  AND (name LIKE '%RTX%' OR name LIKE '%RX %' OR name LIKE '%지포스%' OR name LIKE '%라데온%');
-- Gen bonus
UPDATE parts SET popularity_score = popularity_score + 10 WHERE is_deleted=0 AND category='GPU'
  AND (name LIKE '%4060%' OR name LIKE '%4070%' OR name LIKE '%4080%' OR name LIKE '%4090%'
    OR name LIKE '%5060%' OR name LIKE '%5070%' OR name LIKE '%5080%' OR name LIKE '%5090%'
    OR name LIKE '%7600%' OR name LIKE '%7700%' OR name LIKE '%7800%' OR name LIKE '%7900%');

-- ---- MOTHERBOARD ----
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='MOTHERBOARD'
  AND lowest_price BETWEEN 100000 AND 300000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='MOTHERBOARD'
  AND lowest_price BETWEEN 300001 AND 600000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='MOTHERBOARD'
  AND lowest_price BETWEEN 50000 AND 99999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='MOTHERBOARD'
  AND (lowest_price < 50000 OR lowest_price > 600000);
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='MOTHERBOARD'
  AND (name LIKE '%ASUS%' OR name LIKE '%MSI%' OR name LIKE '%기가바이트%' OR name LIKE '%GIGABYTE%' OR name LIKE '%ASRock%');
UPDATE parts SET popularity_score = popularity_score + 10 WHERE is_deleted=0 AND category='MOTHERBOARD'
  AND (name LIKE '%B650%' OR name LIKE '%B760%' OR name LIKE '%X670%' OR name LIKE '%Z790%'
    OR name LIKE '%B850%' OR name LIKE '%X870%' OR name LIKE '%Z890%');

-- ---- RAM ----
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='RAM'
  AND lowest_price BETWEEN 50000 AND 150000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='RAM'
  AND lowest_price BETWEEN 150001 AND 300000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='RAM'
  AND lowest_price BETWEEN 20000 AND 49999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='RAM'
  AND (lowest_price < 20000 OR lowest_price > 300000);
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='RAM'
  AND (name LIKE '%삼성%' OR name LIKE '%Samsung%' OR name LIKE '%G.SKILL%' OR name LIKE '%지스킬%'
    OR name LIKE '%커세어%' OR name LIKE '%CORSAIR%' OR name LIKE '%킹스톤%' OR name LIKE '%Kingston%');
UPDATE parts SET popularity_score = popularity_score + 10 WHERE is_deleted=0 AND category='RAM'
  AND name LIKE '%DDR5%';

-- ---- SSD ----
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='SSD'
  AND lowest_price BETWEEN 70000 AND 200000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='SSD'
  AND lowest_price BETWEEN 200001 AND 400000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='SSD'
  AND lowest_price BETWEEN 30000 AND 69999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='SSD'
  AND (lowest_price < 30000 OR lowest_price > 400000);
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='SSD'
  AND (name LIKE '%삼성%' OR name LIKE '%Samsung%' OR name LIKE '%SK하이닉스%'
    OR name LIKE '%WD%' OR name LIKE '%웨스턴디지털%');
UPDATE parts SET popularity_score = popularity_score + 10 WHERE is_deleted=0 AND category='SSD'
  AND (name LIKE '%NVMe%' OR name LIKE '%nvme%' OR name LIKE '%990%' OR name LIKE '%980%');

-- ---- POWER_SUPPLY ----
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='POWER_SUPPLY'
  AND lowest_price BETWEEN 60000 AND 150000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='POWER_SUPPLY'
  AND lowest_price BETWEEN 150001 AND 300000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='POWER_SUPPLY'
  AND lowest_price BETWEEN 30000 AND 59999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='POWER_SUPPLY'
  AND (lowest_price < 30000 OR lowest_price > 300000);
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='POWER_SUPPLY'
  AND (name LIKE '%시소닉%' OR name LIKE '%Seasonic%' OR name LIKE '%마이크로닉스%'
    OR name LIKE '%슈퍼플라워%' OR name LIKE '%FSP%' OR name LIKE '%CORSAIR%' OR name LIKE '%커세어%');
UPDATE parts SET popularity_score = popularity_score + 10 WHERE is_deleted=0 AND category='POWER_SUPPLY'
  AND (name LIKE '%80+%' OR name LIKE '%80플러스%' OR name LIKE '%Gold%' OR name LIKE '%골드%' OR name LIKE '%풀모듈러%');

-- ---- CASE ----
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='CASE'
  AND lowest_price BETWEEN 50000 AND 150000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='CASE'
  AND lowest_price BETWEEN 150001 AND 300000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='CASE'
  AND lowest_price BETWEEN 20000 AND 49999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='CASE'
  AND (lowest_price < 20000 OR lowest_price > 300000);
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='CASE'
  AND (name LIKE '%리안리%' OR name LIKE '%LIAN LI%' OR name LIKE '%NZXT%' OR name LIKE '%프렉탈%'
    OR name LIKE '%Fractal%' OR name LIKE '%다크플래쉬%' OR name LIKE '%앱코%');

-- ---- COOLER ----
UPDATE parts SET popularity_score = 50 WHERE is_deleted=0 AND category='COOLER'
  AND lowest_price BETWEEN 30000 AND 100000;
UPDATE parts SET popularity_score = 40 WHERE is_deleted=0 AND category='COOLER'
  AND lowest_price BETWEEN 100001 AND 200000;
UPDATE parts SET popularity_score = 30 WHERE is_deleted=0 AND category='COOLER'
  AND lowest_price BETWEEN 15000 AND 29999;
UPDATE parts SET popularity_score = 10 WHERE is_deleted=0 AND category='COOLER'
  AND (lowest_price < 15000 OR lowest_price > 200000);
UPDATE parts SET popularity_score = popularity_score + 20 WHERE is_deleted=0 AND category='COOLER'
  AND (name LIKE '%녹투아%' OR name LIKE '%Noctua%' OR name LIKE '%써멀라이트%' OR name LIKE '%Thermalright%'
    OR name LIKE '%딥쿨%' OR name LIKE '%DeepCool%' OR name LIKE '%NZXT%' OR name LIKE '%커세어%');

-- Index for sorting
CREATE INDEX idx_parts_popularity ON parts (popularity_score DESC, lowest_price ASC);
