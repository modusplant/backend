-- 검색 정확도 향상을 위해 외래 식물명의 음차 표기를 개별 통일
-- 표준국어대사전 표제어가 있으면 그 표기(거베라, 히아신스, 튤립나무, 코차밤바)를 따르고, 없으면 다음 순서를 따름
--   - 재배품종의 국명과 속 국명이 다르면 속 국명의 표기(페튜니아, 안수리움, 심포리카르포스, 레우코토이, 크산토케라스, 수사셀라)
--   - 그 밖에는 재배품종의 국명과 품종명에서 가장 많이 쓰인 표기(예: 산세비에리아 → 산세베리아)
--   - 사용 수가 같으면 속 국명의 표기(아라우카리아, 도리티스, 스테노카르푸스, 옥시덴드룸)
--   - 사용 수가 같고 속 국명과 무관한 품종명 부분이면 품종명 원어의 음차에 맞는 표기(호소키마키)

-- 1. 국명 통일 (자생종은 표준국어대사전 표제어와 다른 표기인 거버라, 히야신스만 정정하고, 우리말 이명은 유지)
-- 같은 레코드의 오기 '줄무의'(줄무늬)도 함께 정정
UPDATE public.plant
SET korean_name = correction.new_korean_name,
    updated_at  = NOW()
FROM (VALUES ('거버라', 'Gerbera jamesonii Adlam', '거베라'),
             ('골든 아펠둔 튜울립', 'Tulipa ''Golden Apeldoorn''', '골든 아펠둔 튤립'),
             ('구즈마나아 디씨티플로라', 'Guzmania dissitiflora (Andre) L.B.Sm.', '구즈마니아 디씨티플로라'),
             ('다비시애레우코토에', 'Leucothoe davisiae Torr. ex A. Gray', '다비시애레우코토이'),
             ('라케모사레우코토에', 'Leucothoe racemosa A.Gray', '라케모사레우코토이'),
             ('래비가투스심포리카포스', 'Symphoricarpos albus var. laevigatus (Fernald) G.N.Jones', '래비가투스심포리카르포스'),
             ('레우코토에', 'Leucothoe fontanesiana (Steud.) Sleumer', '레우코토이'),
             ('레우코토에 나나', 'Leucothoe fontanesiana ''Nana''', '레우코토이 나나'),
             ('레우코토에 레인보우', 'Leucothoe fontanesiana ''Rainbow''', '레우코토이 레인보우'),
             ('루브라사라케니아', 'Sarracenia rubra Walter', '루브라사라세니아'),
             ('브라질구와바', 'Psidium guineense Sw.', '브라질구아바'),
             ('블랙 패롯트 튜울립', 'Tulipa ''Black Parrot''', '블랙 패롯트 튤립'),
             ('산세비에리아 스투키', 'Sansevieria trifasciata var. laurentii (DeWild.) N.E.Br.', '산세베리아 스투키'),
             ('소르비폴리움크산토세라스', 'Xanthoceras sorbifolium Bunge', '소르비폴리움크산토케라스'),
             ('쉐프레라 약티노필라', 'Schefflera actinophylla (Endl.) Harms', '쉐플레라 약티노필라'),
             ('쉐프레라아르보리콜라 바리에가타', 'Schefflera venulosa (Wight & Arn.) Harms', '쉐플레라아르보리콜라 바리에가타'),
             ('쉐프렐라 베눌로사', 'Schefflera venulosa (Wight & Arn.) Harms', '쉐플레라 베눌로사'),
             ('스케르제리아눔안스리움', 'Anthurium scherzerianum Schott', '스케르제리아눔안수리움'),
             ('스테노카프푸스 시누아투스', 'Stenocarpus sinuatus Endl.', '스테노카르푸스 시누아투스'),
             ('시서스', 'Cissus antarctica Vent.', '시수스'),
             ('심포리카포스', 'Symphoricarpos orbiculatus Moench', '심포리카르포스'),
             ('심포리카포스 폴리스 배리에가티스', 'Symphoricarpos orbiculatus ''Foliis Variegatis''', '심포리카르포스 폴리스 배리에가티스'),
             ('아로니에', 'Aronia arbutifolia (L.) Pers.', '아로니아'),
             ('아로카리아', 'Araucaria heterophylla (Salisb.) Franco', '아라우카리아'),
             ('아이비 제라니움', 'Pelargonium xscarboroviae Sweet', '아이비 제라늄'),
             ('악실라리스레우코토에', 'Leucothoe axillaris D.Don', '악실라리스레우코토이'),
             ('안드래아눔안스리움', 'Anthurium andraeanum Linden', '안드래아눔안수리움'),
             ('안슈리움 코카밤바', 'Anthurium ''Cochabamba''', '안수리움 코차밤바'),
             ('안스리움', 'Anthurium andraeanum Linden', '안수리움'),
             ('안스리움 코차밤바', 'Anthurium ''Cochabamba''', '안수리움 코차밤바'),
             ('옥시덴드론', 'Oxydendrum arboreum DC.', '옥시덴드룸'),
             ('왈테리레우코토에', 'Leucothoe walteri (Willd.) N. C. Melvin', '왈테리레우코토이'),
             ('원통형 산세비에리아', 'Sansevieria cylindrica Bojer', '원통형 산세베리아'),
             ('인플렉사수시셀라 프로스티드 펄스', 'Succisella inflexa ''Frosted Pearls''', '인플렉사수사셀라 프로스티드 펄스'),
             ('청엽 크리프탄서스', 'Cryptanthus beuckeri E.Morren', '청엽 크리프탄투스'),
             ('칼라데아 로세오픽타', 'Calathea roseopicta (Linden) Regel', '칼라테아 로세오픽타'),
             ('칼라데아 리체이', 'Calathea lietzei E.Morren', '칼라테아 리체이'),
             ('칼라데아 마코야나', 'Calathea makoyana E.Morren', '칼라테아 마코야나'),
             ('칼라에아 픽투라타 반덴헥케이', 'Calathea picturata ''Vandenheckei''', '칼라테아 픽투라타 반덴헥케이'),
             ('칼란코에 블러쓰펠디아나', 'Kalanchoe blossfeldiana ''Hwahong''', '칼랑코에 블러쓰펠디아나'),
             ('케이스케이레우코토에', 'Leucothoe keiskei Miq.', '케이스케이레우코토이'),
             ('크라우시아나셀라지넬라', 'Selaginella kraussiana (Kunze) A.Braun', '크라우시아나셀라기넬라'),
             ('크리스탈리눔안스리움', 'Anthurium crystallinum Linden & Andre', '크리스탈리눔안수리움'),
             ('크리프탄서스 베우케리', 'Cryptanthus beuckeri E.Morren', '크리프탄투스 베우케리'),
             ('크리프탄서스 비비타투스 핑크 스타아라이트', 'Cryptanthus bivittatus ''Pink Starlight''', '크리프탄투스 비비타투스 핑크 스타아라이트'),
             ('크리프탄스스 비비타투스 마이놀', 'Cryptanthus bivittatus ''Minor''', '크리프탄투스 비비타투스 마이놀'),
             ('클루시아나 튜울립', 'Tulipa clusiana DC.', '클루시아나 튤립'),
             ('타르다 튜울립', 'Tulipa tarda Stapf', '타르다 튤립'),
             ('파셀리', 'Petroselinum crispum (Mill.) Nyman', '파슬리'),
             ('페다토라디아툼안스리움', 'Anthurium pedato-radiatum Schott', '페다토라디아툼안수리움'),
             ('페투니아', 'Petunia x hybrida E.Vilm.', '페튜니아'),
             ('페투니아 글로리 라이트 핑크', 'Petunia xhybrida ''Glory Light Pink''', '페튜니아 글로리 라이트 핑크'),
             ('페투니아 글로리 라이트 핑크 베인', 'Petunia xhybrida ''Glory Light Pink Vein''', '페튜니아 글로리 라이트 핑크 베인'),
             ('페투니아 글로리 레드', 'Petunia xhybrida ''Glory Red''', '페튜니아 글로리 레드'),
             ('페투니아 글로리 레이트 레드', 'Petunia xhybrida ''Glory Light Red''', '페튜니아 글로리 레이트 레드'),
             ('페투니아 글로리 블루', 'Petunia xhybrida ''Glory Blue''', '페튜니아 글로리 블루'),
             ('페투니아 글로리 에이프리코트 베인', 'Petunia xhybrida ''Glory Apricot Vein''', '페튜니아 글로리 에이프리코트 베인'),
             ('페투니아 글로리 퍼플', 'Petunia xhybrida ''Glory Purple''', '페튜니아 글로리 퍼플'),
             ('페투니아 글로리 플럼 베인', 'Petunia xhybrida ''Glory Plum Vein''', '페튜니아 글로리 플럼 베인'),
             ('페투니아 글로리 핑크 퍼플', 'Petunia xhybrida ''Glory Pink Purple''', '페튜니아 글로리 핑크 퍼플'),
             ('페투니아 글로리 화이트', 'Petunia xhybrida ''Glory White''', '페튜니아 글로리 화이트'),
             ('페투니아 밀레니엄 로즈', 'Petunia xhybrida ''Millenium Rose''', '페튜니아 밀레니엄 로즈'),
             ('페투니아 밀레니엄 화이트', 'Petunia xhybrida ''Millenium White''', '페튜니아 밀레니엄 화이트'),
             ('페투니아 비너스 라이트 옐로', 'Petunia xhybrida ''Venus Light Yellow''', '페튜니아 비너스 라이트 옐로'),
             ('페투니아 비너스 바이올렛', 'Petunia xhybrida ''Venus Violet''', '페튜니아 비너스 바이올렛'),
             ('페투니아 비너스 새먼', 'Petunia xhybrida ''Venus Salmon''', '페튜니아 비너스 새먼'),
             ('페투니아 비너스 핑크 퍼플', 'Petunia xhybrida ''Venus Pink Purple''', '페튜니아 비너스 핑크 퍼플'),
             ('페투니아 사피니아 시리즈', 'Petunia Surfinia Series', '페튜니아 사피니아 시리즈'),
             ('프리씨마 튜울립', 'Tulipa ''Purissima''', '프리씨마 튤립'),
             ('피라칸사 아탈란치오이데스', 'Pyracantha atalantioides (Hance) Stapf', '피라칸다 아탈란치오이데스'),
             ('호소기마니 크로톤', 'Codiaeum variegatum ''Hosokimaki''', '호소키마키 크로톤'),
             ('후쿠시아 디스플레이', 'Fuchsia hybrida ''Display''', '후크시아 디스플레이'),
             ('히야신스', 'Hyacinthus orientalis L.', '히아신스'),
             ('무늬산세비에리아', 'Sansevieria trifasciata var. laurentii (DeWild.) N.E.Br.', '무늬산세베리아'),
             ('산세비에리아', 'Sansevieria trifasciata Prain', '산세베리아'),
             ('분홍 줄무의 칼라데아', 'Calathea majestica ''Roseo-lineata''', '분홍 줄무늬 칼라테아'),
             ('분홍줄무늬 칼라데아', 'Calathea majestica ''Roseo-lineata''', '분홍줄무늬 칼라테아'),
             ('스파티필럼 파티시', 'Spathiphyllum patinii (R. Hogg) N.E.Br.', '스파티필룸 파티시'),
             ('아데니', 'Adenium obesum Roem. & Schult.', '아데니움'),
             ('참 콜치컴', 'Colchicum autumnale L.', '참 콜치쿰'),
             ('콜치컴', 'Colchicum autumnale L.', '콜치쿰'),
             ('넝쿨씨서스', 'Cissus discolor Blume', '넝쿨시수스'),
             ('크립탄서스', 'Cryptanthus acaulis Beer', '크리프탄투스')
     ) AS correction(korean_name, scientific_name, new_korean_name)
WHERE plant.korean_name = correction.korean_name
  AND plant.scientific_name = correction.scientific_name;

-- 2. 위 규칙이 레코드에서 가장 많이 쓰인 표기(안스리움, 심포리카포스, 페투니아, 레우코토에, 히야신스)를 바꾼 경우,
-- 그 표기로도 검색되도록 통일 전 국명의 레코드를 추가 (같은 국명과 학명의 레코드가 여럿이면 하나만 추가)
INSERT INTO public.plant (id, scientific_name_id, scientific_name, korean_name, family_scientific_name,
                          family_korean_name, genus_scientific_name, genus_korean_name, species_name,
                          subspecies_name, resource_type)
SELECT (SELECT COALESCE(MAX(id), -1) FROM public.plant) + ROW_NUMBER() OVER (ORDER BY original.id),
       original.scientific_name_id,
       original.scientific_name,
       original.common_korean_name,
       original.family_scientific_name,
       original.family_korean_name,
       original.genus_scientific_name,
       original.genus_korean_name,
       original.species_name,
       original.subspecies_name,
       original.resource_type
FROM (SELECT DISTINCT ON (plant.korean_name, plant.scientific_name) plant.*, addition.common_korean_name
      FROM public.plant
      JOIN (VALUES ('다비시애레우코토이', 'Leucothoe davisiae Torr. ex A. Gray', '다비시애레우코토에'),
                   ('라케모사레우코토이', 'Leucothoe racemosa A.Gray', '라케모사레우코토에'),
                   ('래비가투스심포리카르포스', 'Symphoricarpos albus var. laevigatus (Fernald) G.N.Jones', '래비가투스심포리카포스'),
                   ('레우코토이', 'Leucothoe fontanesiana (Steud.) Sleumer', '레우코토에'),
                   ('레우코토이 나나', 'Leucothoe fontanesiana ''Nana''', '레우코토에 나나'),
                   ('레우코토이 레인보우', 'Leucothoe fontanesiana ''Rainbow''', '레우코토에 레인보우'),
                   ('스케르제리아눔안수리움', 'Anthurium scherzerianum Schott', '스케르제리아눔안스리움'),
                   ('심포리카르포스', 'Symphoricarpos orbiculatus Moench', '심포리카포스'),
                   ('심포리카르포스 폴리스 배리에가티스', 'Symphoricarpos orbiculatus ''Foliis Variegatis''', '심포리카포스 폴리스 배리에가티스'),
                   ('악실라리스레우코토이', 'Leucothoe axillaris D.Don', '악실라리스레우코토에'),
                   ('안드래아눔안수리움', 'Anthurium andraeanum Linden', '안드래아눔안스리움'),
                   ('안수리움', 'Anthurium andraeanum Linden', '안스리움'),
                   ('안수리움 코차밤바', 'Anthurium ''Cochabamba''', '안스리움 코차밤바'),
                   ('왈테리레우코토이', 'Leucothoe walteri (Willd.) N. C. Melvin', '왈테리레우코토에'),
                   ('케이스케이레우코토이', 'Leucothoe keiskei Miq.', '케이스케이레우코토에'),
                   ('크리스탈리눔안수리움', 'Anthurium crystallinum Linden & Andre', '크리스탈리눔안스리움'),
                   ('페다토라디아툼안수리움', 'Anthurium pedato-radiatum Schott', '페다토라디아툼안스리움'),
                   ('페튜니아', 'Petunia x hybrida E.Vilm.', '페투니아'),
                   ('페튜니아 글로리 라이트 핑크', 'Petunia xhybrida ''Glory Light Pink''', '페투니아 글로리 라이트 핑크'),
                   ('페튜니아 글로리 라이트 핑크 베인', 'Petunia xhybrida ''Glory Light Pink Vein''', '페투니아 글로리 라이트 핑크 베인'),
                   ('페튜니아 글로리 레드', 'Petunia xhybrida ''Glory Red''', '페투니아 글로리 레드'),
                   ('페튜니아 글로리 레이트 레드', 'Petunia xhybrida ''Glory Light Red''', '페투니아 글로리 레이트 레드'),
                   ('페튜니아 글로리 블루', 'Petunia xhybrida ''Glory Blue''', '페투니아 글로리 블루'),
                   ('페튜니아 글로리 에이프리코트 베인', 'Petunia xhybrida ''Glory Apricot Vein''', '페투니아 글로리 에이프리코트 베인'),
                   ('페튜니아 글로리 퍼플', 'Petunia xhybrida ''Glory Purple''', '페투니아 글로리 퍼플'),
                   ('페튜니아 글로리 플럼 베인', 'Petunia xhybrida ''Glory Plum Vein''', '페투니아 글로리 플럼 베인'),
                   ('페튜니아 글로리 핑크 퍼플', 'Petunia xhybrida ''Glory Pink Purple''', '페투니아 글로리 핑크 퍼플'),
                   ('페튜니아 글로리 화이트', 'Petunia xhybrida ''Glory White''', '페투니아 글로리 화이트'),
                   ('페튜니아 밀레니엄 로즈', 'Petunia xhybrida ''Millenium Rose''', '페투니아 밀레니엄 로즈'),
                   ('페튜니아 밀레니엄 화이트', 'Petunia xhybrida ''Millenium White''', '페투니아 밀레니엄 화이트'),
                   ('페튜니아 비너스 라이트 옐로', 'Petunia xhybrida ''Venus Light Yellow''', '페투니아 비너스 라이트 옐로'),
                   ('페튜니아 비너스 바이올렛', 'Petunia xhybrida ''Venus Violet''', '페투니아 비너스 바이올렛'),
                   ('페튜니아 비너스 새먼', 'Petunia xhybrida ''Venus Salmon''', '페투니아 비너스 새먼'),
                   ('페튜니아 비너스 핑크 퍼플', 'Petunia xhybrida ''Venus Pink Purple''', '페투니아 비너스 핑크 퍼플'),
                   ('페튜니아 사피니아 시리즈', 'Petunia Surfinia Series', '페투니아 사피니아 시리즈'),
                   ('히아신스', 'Hyacinthus orientalis L.', '히야신스')
           ) AS addition(korean_name, scientific_name, common_korean_name)
        ON plant.korean_name = addition.korean_name
       AND plant.scientific_name = addition.scientific_name
      ORDER BY plant.korean_name, plant.scientific_name, plant.id) AS original;

-- 3. 품종명 통일 (오기 '물래나물'(물레나물), '수례국화'(수레국화)도 함께 정정)
UPDATE public.plant_variety
SET variety_name = correction.new_variety_name,
    updated_at   = NOW()
FROM (VALUES ('구와바잎', '구아바잎'),
             ('네오레게리아', '네오레겔리아'),
             ('도리테스', '도리티스'),
             ('메타세퀘이아', '메타세쿼이아'),
             ('물래나물', '물레나물'),
             ('미니후쿠시아', '미니후크시아'),
             ('브리세아', '브리에세아'),
             ('수례국화', '수레국화'),
             ('스파티필럼', '스파티필룸'),
             ('오니소갈럼', '오니소갈룸'),
             ('튜립나무', '튤립나무'),
             ('후쿠시아', '후크시아'),
             ('히야신스', '히아신스')
     ) AS correction(variety_name, new_variety_name)
WHERE plant_variety.variety_name = correction.variety_name;

-- 4. 속 국명을 표준국어대사전 표제어의 표기로 정정
UPDATE public.plant
SET genus_korean_name = correction.new_genus_korean_name,
    updated_at        = NOW()
FROM (VALUES ('거버라속', '거베라속'),
             ('히야신스속', '히아신스속'),
             ('튜울립나무속', '튤립나무속')
     ) AS correction(genus_korean_name, new_genus_korean_name)
WHERE plant.genus_korean_name = correction.genus_korean_name;
