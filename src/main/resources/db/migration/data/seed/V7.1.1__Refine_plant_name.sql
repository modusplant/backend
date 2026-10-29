-- 검색 정확도 향상을 위해 plant.korean_name, plant.family_korean_name, plant.genus_korean_name,
-- plant_variety.variety_name의 식물명을 정제
-- 학명과 맞지 않는 국명 정정은 V7.1.2, 표기 통일은 V7.1.3, 중복 레코드 제거는 V7.1.4에서 처리

-- 1. 식물명으로 유효하지 않은 레코드 개별 삭제
--   - '마란타?': 학명(Marshallia grandiflora)과 무관한 국명
--   - '일일초?': 학명(Viola hederacea)과 무관한 국명 (일일초는 Lochnera rosea 레코드로 별도 존재)
--   - '미역방&#54553;', '털부&#49653;이사초': HTML 엔티티로 깨진 국명
DELETE FROM public.plant
WHERE (korean_name, scientific_name) IN (('마란타?', 'Marshallia grandiflora Beadle & F.E.Boynton'),
                                         ('일일초?', 'Viola hederacea Labill.'),
                                         ('미역방&#54553;', 'Peucedanum japonicum Thunb.'),
                                         ('털부&#49653;이사초', 'Carex lasiolepis Franch.'));

--   - 'E-오크': 무늬목(목재 가공품)의 명칭으로, 품종명이 아님
--   - 목재류 중 식물명으로 검색되기 어렵거나 다른 식물로 오인되는 명칭
--     - 목재 무역명의 음차: '레드파인', '화이트퍼', '제루통', '말라스', '오쿠메'
--     - 오기: '시이더'(시더), '월낫'(월넛)
--     - 대응하는 식물을 확인할 수 없는 명칭: '주홍나무', '에투입', '가링'
--     - 여러 속의 목재를 묶은 무역 분류명: '라왕류', '라왕'
--     - 식물명의 일부만 남은 명칭: '느릅'(느릅나무), '로즈'(로즈우드, 장미로 오인됨)
DELETE FROM public.plant_variety
WHERE variety_name IN ('E-오크', '레드파인', '화이트퍼', '제루통', '말라스', '오쿠메', '시이더', '월낫', '주홍나무', '에투입',
                       '가링', '라왕류', '라왕', '느릅', '로즈');

-- 2. 작은따옴표, 큰따옴표, ®, 물음표, &nbsp; 및 앞뒤 공백 제거 (예: 제라늄 '캐럴' → 제라늄 캐럴)
UPDATE public.plant
SET korean_name        = btrim(regexp_replace(korean_name, '[''"®?]|&nbsp;', '', 'g')),
    family_korean_name = btrim(regexp_replace(family_korean_name, '[''"®?]|&nbsp;', '', 'g')),
    genus_korean_name  = btrim(regexp_replace(genus_korean_name, '[''"®?]|&nbsp;', '', 'g')),
    updated_at         = NOW()
WHERE (korean_name, family_korean_name, genus_korean_name) IS DISTINCT FROM
      (btrim(regexp_replace(korean_name, '[''"®?]|&nbsp;', '', 'g')),
       btrim(regexp_replace(family_korean_name, '[''"®?]|&nbsp;', '', 'g')),
       btrim(regexp_replace(genus_korean_name, '[''"®?]|&nbsp;', '', 'g')));

UPDATE public.plant_variety
SET variety_name = btrim(regexp_replace(variety_name, '[''"®?]|&nbsp;', '', 'g')),
    updated_at   = NOW()
WHERE variety_name <> btrim(regexp_replace(variety_name, '[''"®?]|&nbsp;', '', 'g'));

-- 3. 국명의 출처 표기인 (중), (북)과 그 오타인 (븍) 제거 (예: 가는할미꽃(중) → 가는할미꽃)
-- 같은 국명을 구분하는 괄호(예: 밤(미국계))는 유지
UPDATE public.plant
SET korean_name = btrim(regexp_replace(korean_name, '\((중|북|븍)\)', '', 'g')),
    updated_at  = NOW()
WHERE korean_name ~ '\((중|북|븍)\)';

-- 별칭을 담은 괄호는 쉼표로 이어진 국명으로 바꿔, 4.에서 서로 다른 레코드로 분할 (예: 독구리란(노리나) → 독구리란 / 노리나)
UPDATE public.plant
SET korean_name = '독구리란, 노리나',
    updated_at  = NOW()
WHERE (korean_name, scientific_name) = ('독구리란(노리나)', 'Beaucarnea recurvata Lem.');

-- 같은 국명의 다른 레코드가 없어 구분 역할을 하지 않는 계통 표기 괄호 제거 (예: 호소기마니 크로톤(나선엽계) → 호소기마니 크로톤)
UPDATE public.plant
SET korean_name = '호소기마니 크로톤',
    updated_at  = NOW()
WHERE (korean_name, scientific_name) = ('호소기마니 크로톤(나선엽계)', 'Codiaeum variegatum ''Hosokimaki''');

-- Silene 속의 국명은 '끈끈이장구채속'이므로, 이명을 병기한 괄호 제거
UPDATE public.plant
SET genus_korean_name = '끈끈이장구채속',
    updated_at        = NOW()
WHERE genus_korean_name = '끈끈이장구채속(장구채속)';

-- 4. 쉼표로 이어진 여러 국명을 서로 다른 레코드로 분할 (예: 불상화, 히비스커스 → 불상화 / 히비스커스)
-- 빈 국명(예: 크립탄서스,?에서 물음표 제거 후 남은 쉼표 뒤)은 제외하고, 같은 레코드 안의 중복 국명은 하나만 유지
INSERT INTO public.plant (id, scientific_name_id, scientific_name, korean_name, family_scientific_name,
                          family_korean_name, genus_scientific_name, genus_korean_name, species_name,
                          subspecies_name, resource_type)
SELECT (SELECT COALESCE(MAX(id), -1) FROM public.plant) + ROW_NUMBER() OVER (ORDER BY split.id, split.ordinality),
       split.scientific_name_id,
       split.scientific_name,
       split.split_korean_name,
       split.family_scientific_name,
       split.family_korean_name,
       split.genus_scientific_name,
       split.genus_korean_name,
       split.species_name,
       split.subspecies_name,
       split.resource_type
FROM (SELECT DISTINCT ON (plant.id, btrim(piece.name)) plant.*,
                                                       btrim(piece.name) AS split_korean_name,
                                                       piece.ordinality
      FROM public.plant
      CROSS JOIN LATERAL unnest(string_to_array(plant.korean_name, ',')) WITH ORDINALITY AS piece(name, ordinality)
      WHERE plant.korean_name LIKE '%,%'
        AND btrim(piece.name) <> ''
      ORDER BY plant.id, btrim(piece.name), piece.ordinality) AS split;

DELETE FROM public.plant
WHERE korean_name LIKE '%,%';

-- '백하/과응'은 '과응'이 확인되지 않는 풍란 품종명이므로 분리하지 않고, 확인되는 품종명 '백하'만 유지
UPDATE public.plant_variety
SET variety_name = '백하',
    updated_at   = NOW()
WHERE variety_name = '백하/과응';
