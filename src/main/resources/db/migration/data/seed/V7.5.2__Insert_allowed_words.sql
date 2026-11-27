-- [WARNING] 비속어 필터링 테스트를 위한 시드 데이터 파일입니다.
-- 욕설 및 혐오 표현이 다수 포함되어 있습니다.
-- 업무상 필요하지 않다면 열람을 삼가시기 바랍니다.

INSERT INTO allowed_word ("word", created_at)
VALUES
('시발점', NOW()),
('시발역', NOW()),
('씨8개', NOW());
