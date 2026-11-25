-- [WARNING] 비속어 필터링 테스트를 위한 시드 데이터 파일입니다.
-- 욕설 및 혐오 표현이 다수 포함되어 있습니다.
-- 업무상 필요하지 않다면 열람을 삼가시기 바랍니다.

INSERT INTO prohibited_word ("word", type, severity, script, created_at)
VALUES
('쒸발', 'GENERAL', 2, 'HANGUL', NOW()),
('싀발', 'GENERAL', 2, 'HANGUL', NOW()),
('씌발', 'GENERAL', 2, 'HANGUL', NOW()),
('쓰벌', 'GENERAL', 2, 'HANGUL', NOW()),

('시빨', 'GENERAL', 2, 'HANGUL', NOW()),
('씨펄', 'GENERAL', 2, 'HANGUL', NOW()),
('시펄', 'GENERAL', 2, 'HANGUL', NOW()),

('씹알', 'GENERAL', 2, 'HANGUL', NOW()),
('씹팔', 'GENERAL', 2, 'HANGUL', NOW()),

('ㄱㅅㄲ', 'GENERAL', 2, 'HANGUL', NOW()),
('ㅄ', 'GENERAL', 2, 'HANGUL', NOW()),
('ㅈㄴ', 'GENERAL', 2, 'HANGUL', NOW()),

('sibal', 'GENERAL', 2, 'LATIN', NOW()),
('shibal', 'GENERAL', 2, 'LATIN', NOW()),
('gaesaekki', 'GENERAL', 2, 'LATIN', NOW()),
('byungsin', 'GENERAL', 2, 'LATIN', NOW()),
('byeongsin', 'GENERAL', 2, 'LATIN', NOW()),
('jonna', 'GENERAL', 2, 'LATIN', NOW()),
('jiral', 'GENERAL', 2, 'LATIN', NOW()),

('씨8', 'GENERAL', 2, 'HANGUL', NOW()),
('개18', 'GENERAL', 2, 'HANGUL', NOW()),
('18놈', 'GENERAL', 2, 'HANGUL', NOW()),

('씨x', 'GENERAL', 2, 'HANGUL', NOW()),

('🖕', 'GENERAL', 2, 'OTHER', NOW());
