-- 검색 결과의 중복을 없애기 위해 V7.1.1 이전부터 있던 중복 레코드와 V7.1.1 ~ V7.1.3에서 생긴 중복 레코드를 제거
-- id와 생성/수정 시각을 제외한 모든 컬럼이 같은 레코드는 id가 가장 작은 레코드만 유지
DELETE FROM public.plant duplicate
USING public.plant original
WHERE duplicate.id > original.id
  AND (duplicate.scientific_name_id, duplicate.scientific_name, duplicate.korean_name,
       duplicate.family_scientific_name, duplicate.family_korean_name, duplicate.genus_scientific_name,
       duplicate.genus_korean_name, duplicate.species_name, duplicate.subspecies_name, duplicate.resource_type)
      IS NOT DISTINCT FROM
      (original.scientific_name_id, original.scientific_name, original.korean_name,
       original.family_scientific_name, original.family_korean_name, original.genus_scientific_name,
       original.genus_korean_name, original.species_name, original.subspecies_name, original.resource_type);
