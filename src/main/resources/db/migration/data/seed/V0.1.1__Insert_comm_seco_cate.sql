BEGIN;

WITH daily_uuid AS (
	SELECT uuid FROM comm_pri_cate WHERE category = '일상'
)
INSERT INTO comm_seco_cate("uuid", pri_cate_uuid, category, "order", created_at)
SELECT
	uuid_generate_v4(),
	daily_uuid.uuid,
	v.category,
	v."order",
	CURRENT_TIMESTAMP
FROM daily_uuid, (VALUES
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

WITH qna_uuid AS (
	SELECT uuid FROM comm_pri_cate WHERE category = 'Q&A'
)
INSERT INTO comm_seco_cate("uuid", pri_cate_uuid, category, "order", created_at)
SELECT
    public.uuid_generate_v4(),
    qna_uuid.uuid,
    v.category,
    v."order",
    CURRENT_TIMESTAMP
FROM qna_uuid, (VALUES
    ('물주기/흙', 0),
    ('잎상태/성장/병충해', 1),
    ('물꽂이/잎꽂이', 2),
    ('삽목/포기 나누기', 3),
    ('분갈이/가지치기', 4),
    ('월동/씨앗', 5),
    ('식물 추천/품종', 6),
    ('기타', 7)
) AS v(category, "order");

WITH tip_uuid AS (
	SELECT uuid FROM comm_pri_cate WHERE category = '팁'
)
INSERT INTO comm_seco_cate("uuid", pri_cate_uuid, category, "order", created_at)
SELECT
    public.uuid_generate_v4(),
    tip_uuid.uuid,
    v.category,
    v."order",
    CURRENT_TIMESTAMP
FROM tip_uuid, (VALUES
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