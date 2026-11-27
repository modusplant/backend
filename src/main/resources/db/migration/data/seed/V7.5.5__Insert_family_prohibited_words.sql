-- [WARNING] 비속어 필터링 테스트를 위한 시드 데이터 파일입니다.
-- 욕설 및 혐오 표현이 다수 포함되어 있습니다.
-- 업무상 필요하지 않다면 열람을 삼가시기 바랍니다.

INSERT INTO prohibited_word ("word", type, severity, script, created_at)
VALUES
('니애미', 'FAMILY', 3, 'HANGUL', NOW()),
('니애비', 'FAMILY', 3, 'HANGUL', NOW()),
('느그애미', 'FAMILY', 3, 'HANGUL', NOW()),
('느그애비', 'FAMILY', 3, 'HANGUL', NOW()),
('느금', 'FAMILY', 3, 'HANGUL', NOW()),
('느금마', 'FAMILY', 3, 'HANGUL', NOW()),
('느개비', 'FAMILY', 3, 'HANGUL', NOW()),
('ㄴㄱㅁ', 'FAMILY', 3, 'HANGUL', NOW()),
('애미없', 'FAMILY', 3, 'HANGUL', NOW()),
('애비없', 'FAMILY', 3, 'HANGUL', NOW()),
('엠창', 'FAMILY', 3, 'HANGUL', NOW()),
('니미', 'FAMILY', 3, 'HANGUL', NOW()),
('니기미', 'FAMILY', 2, 'HANGUL', NOW());
