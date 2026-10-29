-- 검색 결과의 중복을 없애기 위해 V7.1.1 이전부터 있던 중복 레코드와 V7.1.1 ~ V7.1.3에서 생긴 중복 레코드를 제거
-- id와 생성/수정 시각을 제외한 모든 컬럼이 같은 레코드는 id가 가장 작은 레코드만 유지
-- 레코드 쌍을 모두 비교하는 자기 조인 대신 윈도 함수로 그룹별 순번을 매겨, 한 번의 정렬로 중복 레코드를 식별
DELETE FROM public.plant
WHERE id IN (SELECT ranked_plant.id
             FROM (SELECT id,
                          ROW_NUMBER() OVER (PARTITION BY scientific_name_id, scientific_name, korean_name,
                                                          family_scientific_name, family_korean_name,
                                                          genus_scientific_name, genus_korean_name, species_name,
                                                          subspecies_name, resource_type
                                             ORDER BY id) AS row_number
                   FROM public.plant) AS ranked_plant
             WHERE ranked_plant.row_number > 1);
