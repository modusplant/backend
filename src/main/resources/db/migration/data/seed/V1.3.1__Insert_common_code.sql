-- 공통코드 테이블

-- 환경관리 초기데이터 적재
INSERT INTO common_code_group (group_code, group_name, description, created_at) VALUES
    ('SPACE_LOCATION',  '공간 위치',  NULL, NOW()),
    ('LIGHT_DIRECTION', '빛의 방향',  NULL, NOW()),
    ('VENTILATION_TYPE','환기 방식',  NULL, NOW());

-- 환경관리 초기데이터 적재
INSERT INTO common_code (group_code, code, label, sort_order, created_at) VALUES
    ('SPACE_LOCATION',  'BALCONY',          '베란다',         1, NOW()),
    ('SPACE_LOCATION',  'OUTDOOR',          '야외',           2, NOW()),
    ('SPACE_LOCATION',  'INDOOR_WINDOW',    '실내 (창문 있음)', 3, NOW()),
    ('SPACE_LOCATION',  'INDOOR_NO_WINDOW', '실내 (창문 없음)', 4, NOW()),
    ('SPACE_LOCATION',  'CUSTOM',           '직접 입력',       5, NOW()),

    ('LIGHT_DIRECTION', 'SOUTH',  '남향',     1, NOW()),
    ('LIGHT_DIRECTION', 'WEST',   '서향',     2, NOW()),
    ('LIGHT_DIRECTION', 'NORTH',  '북향',     3, NOW()),
    ('LIGHT_DIRECTION', 'EAST',   '동향',     4, NOW()),
    ('LIGHT_DIRECTION', 'CUSTOM', '직접 입력', 5, NOW()),

    ('VENTILATION_TYPE','FAN',        '선풍기',     1, NOW()),
    ('VENTILATION_TYPE','CIRCULATOR', '서큘레이터', 2, NOW()),
    ('VENTILATION_TYPE','NATURAL',    '자연 환기',  3, NOW()),
    ('VENTILATION_TYPE','NONE',       '환기 없음',  4, NOW()),
    ('VENTILATION_TYPE','CUSTOM',     '직접 입력',  5, NOW());

