-- 검색 정확도 향상을 위해 학명과 맞지 않는 plant.korean_name을 개별 정정
-- 재배품종의 국명이 인접한 학명 ID의 레코드에 밀려서 붙은 경우(예: 사임당 무궁화 → Hibiscus syriacus 'Samcheolli')

-- 1. 국명에 맞는 학명의 레코드가 없으면 삭제
--   - '모넬리뚜껑별꽃 겐티안 블루': Anagallis monelli 'Gentian Blue' 레코드 없음
--   - '시클라멘 메디아': 대응하는 Cyclamen 레코드 없음
--   - '안테테 헤그 핑크 포인셋티아': Euphorbia pulcherrima 'Annette Hegg Pink' 레코드 없음
--   - '헬레니움 더블 트러블': Helenium 'Double Trouble' 레코드 없음
--   - '러시안 바이올렛 무궁화': Hibiscus syriacus 'Russian Violet' 레코드 없음
--   - '아르겐테오마르기나타 양호랑가시나무': Ilex aquifolium 'Argenteomarginata' 레코드 없음
--   - '체리소카르파 윈터베리': Ilex verticillata f. chrysocarpa 레코드 없음
--   - '레드 스르라이트 윈터베리': Ilex verticillata 'Red Sprite' 레코드 없음
--   - '아펠둔 에라이트 튜울립': Tulipa 'Apeldoorn''s Elite' 레코드 없음
--   - '브라질 자귀나무', '물방울풀': 대응하는 학명을 확인할 수 없음
--   - '튜울립', '디기탈리스 그란디프로라', '마르멜로': 품종을 특정할 수 없고, 해당 학명 ID에는 이미 맞는 국명이 있음
--   - '블루버드', '수양 유카': 대응하는 학명이 하나로 정해지지 않음
DELETE FROM public.plant
WHERE (korean_name, scientific_name) IN (('모넬리뚜껑별꽃 겐티안 블루', 'Ananas monelli ''Gentian Blue'''),
                                         ('마르멜로', 'Cydonia oblonga ''Orange'''),
                                         ('디기탈리스 그란디프로라', 'Digitalis grandiflora ''Carillon'''),
                                         ('시클라멘 메디아', 'Dodecatheon meadia f. alba J.F.Macbr.'),
                                         ('안테테 헤그 핑크 포인셋티아', 'Euphorbia pulcherrima ''Menorca'''),
                                         ('헬레니움 더블 트러블', 'Hedychium ''Double Trouble'''),
                                         ('러시안 바이올렛 무궁화', 'Hibiscus syriacus ''Gwangbok'''),
                                         ('블루버드', 'Hibiscus syriacus ''Oknyo'''),
                                         ('아르겐테오마르기나타 양호랑가시나무', 'Ilex aquifolium ''Ashford'''),
                                         ('체리소카르파 윈터베리', 'Ilex verticillata ''Bright Horizon'''),
                                         ('레드 스르라이트 윈터베리', 'Ilex verticillata ''Red Cascade'''),
                                         ('브라질 자귀나무', 'Leucanthemum xsuperbum ''Aglaia'''),
                                         ('아펠둔 에라이트 튜울립', 'Tulipa ''Black Parrot'''),
                                         ('튜울립', 'Tulipa ''Jan Van Nes'''),
                                         ('물방울풀', 'Solidago virgaurea subsp. minuta Arc.'),
                                         ('수양 유카', 'Zamioculcas zamiifolia Engl.'));

-- 2. 국명에 맞는 학명의 레코드가 있으면, 해당 학명 ID 레코드의 학명/과/속/종/아종/자원 유형으로 변경 (주석: 변경 후 학명)
-- 갱신 전 상태를 기준으로 한 번에 변경해, 서로 맞물린 정정(예: 망고스틴 28657 → 28656, 마루바 치자나무 28658 → 28657)이 섞이지 않게 함
UPDATE public.plant
SET scientific_name_id     = source.scientific_name_id,
    scientific_name        = source.scientific_name,
    family_scientific_name = source.family_scientific_name,
    family_korean_name     = source.family_korean_name,
    genus_scientific_name  = source.genus_scientific_name,
    genus_korean_name      = source.genus_korean_name,
    species_name           = source.species_name,
    subspecies_name        = source.subspecies_name,
    resource_type          = source.resource_type,
    updated_at             = NOW()
FROM (VALUES ('덴드로비움 오쿨라툼', 'Dendrobium kingianum Bidwill ex Lindl.', 27448), -- Dendrobium fimbriatum var. oculatum
             ('킨기아넘', 'Dendropanax pellucidopunctatus (Hayata) Kaneh.', 27449), -- Dendrobium kingianum Bidwill ex Lindl.
             ('크리프탄서스 베우케리', 'Cryptanthus bivittatus ''Minor''', 27654), -- Cryptanthus beuckeri E.Morren
             ('크리프탄스스 비비타투스 마이놀', 'Cryptanthus bivittatus ''Pink Starlight''', 27655), -- Cryptanthus bivittatus 'Minor'
             ('크리프탄서스 비비타투스 핑크 스타아라이트', 'Cryptanthus bromelioides var. tricolor M.B.Foster', 27656), -- Cryptanthus bivittatus 'Pink Starlight'
             ('골드크레스트', 'Cupressus macrocarpa ''Golden Pillar''', 27722), -- Cupressus macrocarpa 'Goldcrest'
             ('나무토마토', 'Cyphostemma juttae (Dinter & Gilg) Desc.', 27766), -- Cyphomandra betacea (Cav.) Sendtn.
             ('달링토니아 캘리포니아', 'Darmera peltata (Torr. ex Benth.) Voss', 27822), -- Darlingtonia californica Torr.
             ('천사의 나팔꽃', 'Davidia involucrata Baill.', 26202), -- Brugmansia suaveolens (Willd.) Bercht. & C.Presl
             ('드로세라 버만니', 'Drosera capensis L.', 27909), -- Drosera burmanni Vahl
             ('마쿨라타 보리수나무', 'Elaeagnus pungens ''Silver Lining''', 28008), -- Elaeagnus pungens 'Maculata'
             ('입성 무늬 달개비', 'Dicksonia antarctica Labill.', 28087), -- Dichorisandra thyrsiflora 'Variegata'
             ('디펜바키아 오에르스테디', 'Dieffenbachia seguine ''Exotica''', 28096), -- Dieffenbachia oerstedii Schott
             ('디펜바키아 세구이네 엑소티카', 'Dierama adelphicum Hilliard', 28097), -- Dieffenbachia seguine 'Exotica'
             ('디기탈리스 루테아', 'Digitalis xmertonensis B.H.Buxton & C.D.Darl.', 28118), -- Digitalis lutea L.
             ('행운목', 'Dracaena fragrans ''Compacta Purpurea''', 28207), -- Dracaena fragrans 'Massangeana'
             ('드라세나 콤팩타', 'Dracaena fragrans ''Massangeana''', 28206), -- Dracaena fragrans 'Compacta Purpurea'
             ('드라세나 맛상게아나', 'Dracaena fragrans ''Warneckei''', 28207), -- Dracaena fragrans 'Massangeana'
             ('후쿠시아 디스플레이', 'Fuchsia magellanica Lam.', 28259), -- Fuchsia hybrida 'Display'
             ('와룡', 'Eriogonum caespitosum Nutt.', 28354), -- Eriocactus tortuosus Riccob.
             ('꽃기린', 'Euphorbia myrsinites L.', 28491), -- Euphorbia milii DesMoul. ex Boiss.
             ('유포르비아 오베사', 'Euphorbia officinarum subsp. echinus (Hook.f. & Coss.) Vindt', 28494), -- Euphorbia obesa Hook.f.
             ('유포르비아 트리고나', 'Euphorbia viguieri Denis', 28509), -- Euphorbia trigona Haw.
             ('리시안서스', 'Excoecaria cochinchinensis Lour.', 28516), -- Eustoma grandiflorum (Raf.) Shinners
             ('꽃도라지', 'Excoecaria cochinchinensis Lour.', 28516), -- Eustoma grandiflorum (Raf.) Shinners
             ('유스토마', 'Excoecaria cochinchinensis Lour.', 28516), -- Eustoma grandiflorum (Raf.) Shinners
             ('파셀리', 'Ficus benghalensis L.', 31310), -- Petroselinum crispum (Mill.) Nyman
             ('장실금감', 'Fothergilla gardenii L.', 28629), -- Fortunella margarita Swingle
             ('자이언트 스노우드로우프', 'Galanthus nivalis L.', 28648), -- Galanthus elwesii Hook.f.
             ('망고스틴', 'Gardenia jasminoides ''Maruba''', 28656), -- Garcinia mangostana L.
             ('마루바 치자나무', 'Gardenia jasminoides ''Radicans Variegata''', 28657), -- Gardenia jasminoides 'Maruba'
             ('글레코마', 'Gleditsia caspica Desf.', 28986), -- Glechoma hederacea 'Variegata'
             ('무늬병꽃풀', 'Gleditsia caspica Desf.', 28986), -- Glechoma hederacea 'Variegata'
             ('구즈마나아 디씨티플로라', 'Guzmania ''Insignis''', 29021), -- Guzmania dissitiflora (Andre) L.B.Sm.
             ('사임당 무궁화', 'Hibiscus syriacus ''Samcheolli''', 29065), -- Hibiscus syriacus 'Saimdang'
             ('설단심', 'Hibiscus syriacus ''Seongcheon''', 29071), -- Hibiscus syriacus 'Seoldansim'
             ('그린 리플 아이비', 'Hedera helix ''Harald''', 29115), -- Hedera helix 'Green Ripple'
             ('헬리코니아 로스트라타', 'Heliopsis helianthoides var. scabra ''Sommersonne''', 29213), -- Heliconia rostrata Ruiz & Pav.
             ('헬리오트롭', 'Helleborus argutifolius Viv.', 29215), -- Heliotropium arborescens L.
             ('마린 향수초', 'Helleborus argutifolius Viv.', 29215), -- Heliotropium arborescens L.
             ('아덴스 무궁화', 'Hibiscus syriacus ''Asadal''', 29426), -- Hibiscus syriacus 'Ardens'
             ('아사달', 'Hibiscus syriacus ''Asanyo''', 29427), -- Hibiscus syriacus 'Asadal'
             ('배달', 'Hibiscus syriacus ''Baekseol''', 29430), -- Hibiscus syriacus 'Baedal'
             ('홍단심 무궁화', 'Hibiscus syriacus ''Boule De Feu''', 29491), -- Hibiscus syriacus 'Hongtanshim'
             ('본조이아', 'Hibiscus syriacus ''Boule De Feu''', 29435), -- Hibiscus syriacus 'Bonjoia'
             ('다이아나 무궁화', 'Hibiscus syriacus ''Diatokujishiro''', 29462), -- Hibiscus syriacus 'Diana'
             ('동자 무궁화', 'Hibiscus syriacus ''Dorothy Crane''', 29464), -- Hibiscus syriacus 'Dongja'
             ('하마보 무궁화', 'Hibiscus syriacus ''Hanbit''', 29478), -- Hibiscus syriacus 'Hamabo'
             ('헬렌 무궁화', 'Hibiscus syriacus ''Hinomaru''', 29485), -- Hibiscus syriacus 'Helene'
             ('화랑', 'Hibiscus syriacus ''Hyangdan''', 29494), -- Hibiscus syriacus 'Hwarang'
             ('엘레간트시마 무궁화', 'Hibiscus syriacus ''Large White''', 29467), -- Hibiscus syriacus 'Elegantissimus'
             ('나보라 무궁화', 'Hibiscus syriacus ''Naesarang''', 29528), -- Hibiscus syriacus 'Nabora'
             ('폼폰 로우즈', 'Hibiscus syriacus ''Ps 80-1''', 29542), -- Hibiscus syriacus 'Pompon Rouge'
             ('우드 브릿지 무궁화', 'Hibiscus syriacus ''Yeohae''', 29110), -- Hibiscus syriacus 'Woodbridge'
             ('영광 무궁화', 'Hibiscus syriacus ''Yeonhongweol''', 29547), -- Hibiscus syriacus 'Yeongkwang'
             ('리본풀', 'Hosta ''Abiqua Blue Crinkles''', 29561), -- Homalocladium platycladum L.H.Bailey
             ('하이페리쿰 칼미아눔', 'Hypericum kouytchense H.Lev.', 29626), -- Hypericum kalmianum L.
             ('이베리스 움벨라타', 'Ilex ''Accent''', 29654), -- Iberis umbellata L.
             ('골든 킹 유럽호랑가시나무', 'Ilex xaltaclerensis ''Hodginsii''', 29665), -- Ilex xaltaclerensis 'Golden King'
             ('로손니아나 유럽호랑가시나무', 'Ilex xaltaclerensis ''Maderensis''', 29668), -- Ilex xaltaclerensis 'Lawsoniana'
             ('양호랑가시나무', 'Ilex aquifolium ''Amber''', 29679), -- Ilex aquifolium L.
             ('골든 밀크보이 양호랑가시나무', 'Ilex aquifolium ''Golden Queen''', 29716), -- Ilex aquifolium 'Golden Milkboy'
             ('마담 브리오트 양호랑가시나무', 'Ilex ''Maderensisvariegata''', 29731), -- Ilex aquifolium 'Madame Briot'
             ('호야 벨라', 'Huernia zebrina N.E.Br.', 29842), -- Hoya lanceolata subsp. bella (Hook.) D.H.Kent
             ('오바타 아우레아 양호랑가시나무', 'Ilex aquifolium ''Pendula''', 29989), -- Ilex aquifolium 'Ovata Aurea'
             ('임파치엔스', 'Incarvillea delavayi Bureau & Franch.', 30265), -- Impatiens walleriana Hook.f.
             ('아프리칸봉선화', 'Incarvillea delavayi Bureau & Franch.', 30265), -- Impatiens walleriana Hook.f.
             ('이소토마', 'Itea virginica L.', 30375), -- Isotoma axillaris Lindl.
             ('익소라 코키네아', 'Jasione laevis Lam.', 30384), -- Ixora coccinea L.
             ('람프란서스', 'Lantana montevidensis (Spreng.) Briq.', 30453), -- Lampranthus spectabilis N.E.Br.
             ('섬머 스노우플레이크', 'Leucospermum lineare R.Br.', 30530), -- Leucojum aestivum L.
             ('은방울 수선화', 'Leucospermum lineare R.Br.', 30530), -- Leucojum aestivum L.
             ('스카이 로켓', 'Juniperus squamata ''Blue Carpet''', 30660), -- Juniperus scopulorum 'Skyrocket'
             ('스카이로켓향나무', 'Juniperus squamata ''Blue Carpet''', 30660), -- Juniperus scopulorum 'Skyrocket'
             ('블루 스타 둥근 향나무', 'Juniperus squamata ''Prostrata''', 30664), -- Juniperus squamata 'Blue Star'
             ('흰무늬잎 황매화', 'Keteleeria evelyniana Mast.', 30710), -- Kerria japonica 'Picta'
             ('트리토마', 'Koeleria glauca DC.', 30734), -- Kniphofia uvaria (L.) Oken
             ('여광전', 'Laburnum xwatereri ''Vossii''', 30747), -- Krainzia guelzowiana (Werderm.) Backeb.
             ('리갈 백합', 'Lilium ''Sara''', 30853), -- Lilium regale E.H.Wilson
             ('루브룸 녹자 백합', 'Lilium superbum L.', 30864), -- Lilium speciosum var. rubrum Mast. ex Baker
             ('당선형야자', 'Lobelia kalmii L.', 30932), -- Livistona chinensis R.Br.
             ('비로아쟈', 'Lobelia kalmii L.', 30932), -- Livistona chinensis R.Br.
             ('도비로야자 감람수', 'Lobelia kalmii L.', 30932), -- Livistona chinensis R.Br.
             ('붉은꽃인동덩굴', 'Lonicera tatarica ''Arnold Red''', 30965), -- Lonicera sempervirens L.
             ('셈퍼비렌스 인동덩굴', 'Lonicera tatarica ''Arnold Red''', 30965), -- Lonicera sempervirens L.
             ('리코리스 스프렌게리', 'Lysichiton americanus Hulten & H. St. John', 31019), -- Lycoris sprengeri Comes ex Baker
             ('리시마카아 누무라리아', 'Lysimachia punctata ''Alexander''', 31026), -- Lysimachia nummularia L.
             ('타키투스', 'Tamarix parviflora DC.', 31263), -- Tacitus bellus Moran & J.Meyran
             ('페페로미아 카페라타', 'Peperomia clusiifolia ''Jewelry''', 31283), -- Peperomia caperata Yunck.
             ('페페로미아 글라벨라', 'Peperomia orba G.S.Bunting', 31287), -- Peperomia glabella A.Dietr.
             ('파슬리', 'Petunia xhybrida ''Glory Apricot Vein''', 31310), -- Petroselinum crispum (Mill.) Nyman
             ('미니마 서양 측백', 'Thuja occidentalis ''Recurva Nana''', 31596), -- Thuja occidentalis 'Minima'
             ('노란줄 무늬 신서란', 'Photinia beauverdiana var. notabilis (C. K. Schneid.) Rehder & E.H.Wilson', 31669), -- Phormium tenax 'Veitchianum'
             ('백리향', 'Tiarella cordifolia ''Oakleaf''', 15932), -- Thymus quinquecostatus Celak.
             ('토레니아', 'Townsendia condensata Parry', 31777), -- Torenia fournieri E.Fourn.
             ('노랑 무늬 자주색 만년초', 'Trichodiadema bulbosum Schwantes', 31925), -- Tradescantia spathacea 'Vittata'
             ('뱀오이', 'Tricyrtis formosana ''Purple Beauty''', 31931), -- Trichosanthes cucumerina subsp. anguina (L.) Greb.
             ('로열 스타 별목련', 'Magnolia ''Suishoren''', 31988), -- Magnolia stellata 'Royal Star'
             ('블랙 패롯트 튜울립', 'Tulipa clusiana DC.', 32176), -- Tulipa 'Black Parrot'
             ('클루시아나 튜울립', 'Tulipa ''Don Quichotte''', 32180), -- Tulipa clusiana DC.
             ('골든 아펠둔 튜울립', 'Tulipa ''Leen Van Der Mark''', 32188), -- Tulipa 'Golden Apeldoorn'
             ('프리씨마 튜울립', 'Tulipa saxatilis Sieber ex Spreng.', 32200), -- Tulipa 'Purissima'
             ('타르다 튜울립', 'Tulipa urumiensis Stapf', 32208), -- Tulipa tarda Stapf
             ('흰꽃 해당화', 'Roscoea purpurea Sm.', 32343), -- Rosa rugosa 'Alba'
             ('루스쿠스 아쿨레아투스', 'Ruta chalepensis L.', 32380), -- Ruscus aculeatus L.
             ('버베나', 'Vernonia allionii Vill.', 32435), -- Verbena xhybrida Hort. ex Vilm.
             ('메디닐라', 'Melaleuca linariifolia Sm.', 32447), -- Medinilla magnifica Lindl.
             ('층운', 'Mentha aquatica L.', 32460), -- Melocactus amoenus Hoffmanns.
             ('그놈 스위스 둥근 소나무', 'Pinus mugo ''Trompenburg''', 32519), -- Pinus mugo 'Gnom'
             ('사발 미노르', 'Salix alba L.', 32388), -- Sabal minor (Jacq.) Pers.
             ('석화버들', 'Salvia coccinea Juss. ex Murray', 32584), -- Salix udensis 'Sekka'
             ('피스타시오', 'Pittosporum colensoi Hook.f.', 32760), -- Pistacia vera L.
             ('원통형 산세비에리아', 'Sansevieria trifasciata Prain', 32810), -- Sansevieria cylindrica Bojer
             ('무늬산세비에리아', 'Santolina chamaecyparissus L.', 32816), -- Sansevieria trifasciata var. laurentii (DeWild.) N.E.Br.
             ('산세비에리아', 'Santolina chamaecyparissus L.', 32814), -- Sansevieria trifasciata Prain
             ('산세베리아 하니', 'Santolina oblongifolia Boiss.', 32815), -- Sansevieria trifasciata 'Hahnii'
             ('황면국', 'Sapindus drummondii Hook. & Arn.', 32820), -- Sanvitalia procumbens Lam.
             ('누운 백일홍', 'Saponaria ocymoides L.', 32820), -- Sanvitalia procumbens Lam.
             ('빅토리아연꽃', 'Vinca minor ''Argenteovariegata''', 32858), -- Victoria amazonica Sowerby
             ('파라과이 수련', 'Vinca minor ''Illumination''', 32859), -- Victoria cruziana Orb.
             ('은매화', 'Nandina domestica var. leucocarpa Makino', 32882), -- Myrtus communis L.
             ('무늬사사 대나무', 'Plumbago auriculata Lam.', 32967), -- Pleioblastus variegatus Makino
             ('플루메리아 아쿠미나타', 'Podocarpus andinus Poepp. ex Endl.', 32971), -- Plumeria acuminata Ait.
             ('플루메리아', 'Podocarpus andinus Poepp. ex Endl.', 32971), -- Plumeria acuminata Ait.
             ('사라세니아 프지타시나', 'Saruma henryi Oliv.', 32996), -- Sarracenia psittacina Michx.
             ('사라세니아 퍼푸레아', 'Sasa veitchii Rehder', 32997), -- Sarracenia purpurea L.
             ('리파리아포도', 'Vriesea xpoelmanii ''Red King''', 33049), -- Vitis riparia Michx.
             ('리파리아', 'Vriesea xpoelmanii ''Red King''', 33049), -- Vitis riparia Michx.
             ('머스카딘포도', 'Vriesea splendens (Brongn.) Lem.', 33050), -- Vitis rotundifolia Michx.
             ('와싱토니아 필리페라', 'Weigela coraeensis Thunb.', 33055), -- Washingtonia filifera (Linden ex Andre) H. Wendl.
             ('워싱톤 야자', 'Weigela coraeensis Thunb.', 33055), -- Washingtonia filifera (Linden ex Andre) H. Wendl.
             ('네펜데스 알라타', 'Nepenthes hamatus J.R.Turnbull & A.T.Middleton', 32932), -- Nepenthes alata Blanco
             ('네펜데스 메릴리아나', 'Nepenthes rajah Hook.f.', 33088), -- Nepenthes merrilliana Macfarl.
             ('네프로네피스 엑살타타', 'Nerine bowdenii ''Mollie Cowie''', 33104), -- Nephrolepis exaltata (L.) Schott
             ('흑종초', 'Nothofagus dombeyi (Mirb.) Oerst.', 33120), -- Nigella damascena L.
             ('쉐프레라 약티노필라', 'Schefflera elegantissima ''Castor''', 33221), -- Schefflera actinophylla (Endl.) Harms
             ('디지고데카', 'Schima superba Gardner & Champ.', 33224), -- Schefflera elegantissima (Veitch ex Masters) Lowry & Frodin
             ('캐스터 아랄리아', 'Schima molle L.', 33225), -- Schefflera elegantissima 'Castor'
             ('쉐프렐라 베눌로사', 'Schisandra sphenanthera Rehder & E. H. Wilson', 33227), -- Schefflera venulosa (Wight & Arn.) Harms
             ('칼라', 'Zanthoxylum simulans Hance', 33263), -- Zantedeschia aethiopica (L.) Spreng.
             ('바실', 'Oenothera acaulis ''Aurea''', 33351), -- Ocimum basilicum L.
             ('프리물라 불가리스', 'Prinsepia uniflora Batalin', 33539), -- Primula vulgaris Huds.
             ('용왕꽃', 'Protea magnifica Andrews', 33546), -- Protea cynaroides (L.) L.
             ('미국자두나무', 'Prunus armeniaca ''Moorpark''', 33556), -- Prunus americana Marshall
             ('셀라기넬라 크라시아나', 'Sempervivum arachnoideum L.', 33592), -- Selaginella kraussiana (Kunze) A.Braun
             ('셀라기넬라 크라우스씨아나', 'Sempervivum arachnoideum L.', 33592), -- Selaginella kraussiana (Kunze) A.Braun
             ('사랑초', 'Oxytropis campestris (L.) DC.', 33676), -- Oxalis triangularis subsp. papilionacea (Hoffmanns. ex Zucc.) Lourteig
             ('옥시덴드론', 'Oxytropis campestris var. cusickii (Greenm.) Barneby', 33677), -- Oxydendrum arboreum DC.
             ('세네치오 아이비', 'Sequoia sempervirens Endl.', 33757), -- Senecio macroglossus 'Variegatus'
             ('방울선인장', 'Sequoia sempervirens ''Aptos Blue''', 33759), -- Senecio rowleyanus H. Jacobsen
             ('파키스타키스', 'Paeonia daurica Andrews', 33817), -- Pachystachys lutea Nees
             ('판다누스 산데리', 'Pandorea jasminoides (Lindl.) K.Schum.', 33863), -- Pandanus tectorius 'Sanderi'
             ('판다누스 우틸리스', 'Pandorea jasminoides ''Lady Di''', 33864), -- Pandanus utilis Bory
             ('판도레아 자스민', 'Papaver alboroseum Hulten', 33866), -- Pandorea jasminoides (Lindl.) K.Schum.
             ('노랑잎회화나무', 'Sorbaria tomentosa (Lindl.) Rehder', 33939), -- Sophora japonica 'Aurea'
             ('소프로니티스 코키네아', 'Sorbus aria (L.) Crantz', 33941), -- Sophronitis coccinea Rchb.f.
             ('스파티필룸 파틴니', 'Spiraea bella Sims', 33972), -- Spathiphyllum patinii (R. Hogg) N.E.Br.
             ('스파티필럼 파티시', 'Spiraea bella Sims', 33972), -- Spathiphyllum patinii (R. Hogg) N.E.Br.
             ('스파티필룸 왈리시', 'Spiraea cantoniensis ''Flore Pleno''', 33973), -- Spathiphyllum wallisii Hort.
             ('열매시계초', 'Pavonia spinifex (L.) Cav.', 34024), -- Passiflora edulis Sims
             ('과물시계초', 'Pavonia spinifex (L.) Cav.', 34024), -- Passiflora edulis Sims
             ('피라칸사 아탈란치오이데스', 'Pyracantha crenatoserrata (Hance) Rehder', 34041), -- Pyracantha atalantioides (Hance) Stapf
             ('피라미드 영국 참나무', 'Quercus rubra ''Sunshine''', 34079), -- Quercus robur f. fastigiata (Lam.) O.Schwarz
             ('스테노카프푸스 시누아투스', 'Sterculia monosperma Vent.', 34140), -- Stenocarpus sinuatus Endl.
             ('펠라에아 팔카타', 'Peltiphyllum peltatum Engl. in Engl.', 34154), -- Pellaea falcata (R Br.) Fee
             ('펠라에아 로툰디폴리아', 'Pennisetum alopecuroides ''Caudatum''', 34155), -- Pellaea rotundifolia (G.Forst.) Hook.
             ('수박 펠리어니아', 'Pennisetum alopecuroides ''Hameln''', 34156), -- Pellionia daveauana N.E.Br.
             ('데저트피', 'Symphoricarpos albus K. Koch', 34291), -- Swainsona formosa (G.Don) JoyThomps.
             ('싱고니움 그린 골드', 'Syringa emodi ''Aurea''', 34303) -- Syngonium xanthophidium 'Green Gold'
     ) AS correction(korean_name, scientific_name, target_scientific_name_id)
JOIN (SELECT DISTINCT ON (scientific_name_id) *
      FROM public.plant
      ORDER BY scientific_name_id, id) AS source
  ON source.scientific_name_id = correction.target_scientific_name_id
WHERE plant.korean_name = correction.korean_name
  AND plant.scientific_name = correction.scientific_name;
