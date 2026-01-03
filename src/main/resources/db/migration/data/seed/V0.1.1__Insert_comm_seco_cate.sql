BEGIN;

WITH daily_id AS (
	SELECT id FROM comm_pri_cate WHERE category = '일상'
)
INSERT INTO comm_seco_cate(pri_cate_id, category, "order", created_at)
SELECT
    daily_id.id,
	v.category,
	v."order",
	CURRENT_TIMESTAMP
FROM daily_id, (VALUES
    ('관엽/야생화', 0),
    ('제라늄', 1),
    ('베고니아', 2),
    ('다육/선인장', 3),
    ('식충/덩굴/구근', 4),
    ('고사리/이끼/수생', 5),
    ('베란다/정원', 6),
    ('농사/텃밭', 7),
    ('식물 쇼핑', 8),
    ('기타', 9)
) AS v(category, "order");

WITH qna_id AS (
	SELECT id FROM comm_pri_cate WHERE category = 'Q&A'
)
INSERT INTO comm_seco_cate(pri_cate_id, category, "order", created_at)
SELECT
    qna_id.id,
    v.category,
    v."order",
    CURRENT_TIMESTAMP
FROM qna_id, (VALUES
    ('물주기/흙', 0),
    ('잎상태/성장/병충해', 1),
    ('물꽂이/잎꽂이', 2),
    ('삽목/포기 나누기', 3),
    ('분갈이/가지치기', 4),
    ('월동/씨앗', 5),
    ('식물 추천/품종', 6),
    ('기타', 7)
) AS v(category, "order");

WITH tip_id AS (
	SELECT id FROM comm_pri_cate WHERE category = '팁'
)
INSERT INTO comm_seco_cate(pri_cate_id, category, "order", created_at)
SELECT
    tip_id.id,
    v.category,
    v."order",
    CURRENT_TIMESTAMP
FROM tip_id, (VALUES
    ('물주기/흙', 0),
    ('잎상태/성장/병충해', 1),
    ('물꽂이/잎꽂이', 2),
    ('삽목/포기 나누기', 3),
    ('분갈이/가지치기', 4),
    ('월동/씨앗', 5),
    ('식물 추천/품종', 6),
    ('기타', 7)
) AS v(category, "order");

COMMIT;