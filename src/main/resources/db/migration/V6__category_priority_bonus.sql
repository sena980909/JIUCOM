-- V6: Add category priority bonus so CPU/GPU appear first in mixed listings
-- CPU/GPU are the primary components users care about most

-- CPU: +15 priority bonus
UPDATE parts SET popularity_score = popularity_score + 15
WHERE category = 'CPU' AND is_deleted = 0;

-- GPU: +15 priority bonus
UPDATE parts SET popularity_score = popularity_score + 15
WHERE category = 'GPU' AND is_deleted = 0;

-- MOTHERBOARD: +5 (important secondary component)
UPDATE parts SET popularity_score = popularity_score + 5
WHERE category = 'MOTHERBOARD' AND is_deleted = 0;
