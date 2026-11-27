-- [WARNING] 비속어 필터링 테스트를 위한 시드 데이터 파일입니다.
-- 욕설 및 혐오 표현이 다수 포함되어 있습니다.
-- 업무상 필요하지 않다면 열람을 삼가시기 바랍니다.

INSERT INTO allowed_word ("word", created_at)
VALUES
('시발점', NOW()),
('시발역', NOW()),
('씨8개', NOW()),
('개년간', NOW()),
('개년도', NOW()),
('개년계획', NOW()),
('수개년', NOW()),
('등신대', NOW()),
('등신불', NOW()),
('팔등신', NOW()),

('자위대', NOW()),
('자위권', NOW()),
('탄핵소추', NOW()),
('소추권', NOW()),
('소추안', NOW()),
('떡치기', NOW()),
('성교육', NOW()),
('정상위치', NOW()),
('정상위에', NOW()),
('정상위로', NOW()),
('금딸기', NOW()),
('대딸기', NOW()),

('thorny', NOW()),
('milfoil', NOW()),
('pussy willow', NOW()),
('pussywillow', NOW());
