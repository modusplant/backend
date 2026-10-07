package db.migration.data.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 표준 품목 코드 CSV(standard_item_code_001.csv)를 정제하여 plant_variety 테이블에 삽입한다.
 *
 * <p>CSV 사전 가공 이력 (CSV 파일 자체에 이미 반영됨)
 * <br>원본 소스로서 CSV를 갱신할 때 아래 작업을 동일하게 다시 수행하여 데이터 일관성을 보장하도록 한다.
 * <ul>
 *   <li>대분류명이 "농림가공", "농산물종자류"인 레코드는 모두 삭제</li>
 *   <li>소분류명의 "(수입)" 문자열은 모두 삭제</li>
 *   <li>중분류코드 2347의 중분류명 "암"을 "얌"으로 수정 (오타)</li>
 *   <li>중분류명 "해바라기씨"(중분류코드 1631)의 레코드는 모두 삭제 (종자)</li>
 * </ul>
 * 그 외 CSV의 코드는 원본 값을 유지하며, 코드 정규화는 마이그레이션 간 수행한다.
 *
 * <p>마이그레이션 간 정제 순서 (코드의 "[단계 N]" 주석과 대응)
 * <ol>
 *   <li>엑셀 지수 표기로 깨진 코드 복원</li>
 *   <li>엑셀로 인해 유실된 코드 앞자리 0 복원</li>
 *   <li>중분류코드와 소분류코드의 접두어 간 불일치 교정</li>
 *   <li>품종명에서 괄호 문자열 제거</li>
 *   <li>품종명으로 기능할 수 없는 레코드 제외</li>
 *   <li>대표 중분류코드가 아닌 레코드 제외 (1:1 대응 규칙 2)</li>
 *   <li>중분류명 통일 및 분리 (1:1 대응 규칙 1, 규칙 2의 "기타")</li>
 *   <li>독립 중분류코드 부여 (1:1 대응 규칙 1)</li>
 *   <li>품종명 중복 레코드 제거</li>
 * </ol>
 *
 * <p>중분류코드와 중분류명의 1:1 대응 (중분류명을 기준으로 중분류코드를 정함)
 * <ol>
 *   <li>하나의 중분류코드에 여러 중분류명이 존재하는 경우: 가장 합리적인 중분류명이 중분류코드를 유지하고,
 *       나머지 중분류명에는 독립 중분류코드를 부여한다. 단, 같은 식물을 가리키는 중분류명은 하나로 통일한다.</li>
 *   <li>하나의 중분류명에 여러 중분류코드가 존재하는 경우: 가장 합리적인 중분류코드 하나만 남기고,
 *       나머지 중분류코드의 레코드는 모두 제거한다. 단, 포괄 항목인 "기타"는 "기타(대분류명)"으로 중분류명을 분리한다.</li>
 * </ol>
 */
public class V5_2_1__Insert_plant_variety_table_default_data extends BaseJavaMigration {
    // 중분류코드와 소분류코드의 자릿수 (소분류코드 = 중분류코드 4자리 + 일련번호 2자리)
    private static final int MIDDLE_CATEGORY_CODE_LENGTH = 4;
    private static final int VARIETY_CODE_LENGTH = 6;

    // [단계 1] 엑셀이 "2403E3", "26E8"과 같은 코드를 숫자로 해석하여 "2.40E+06" 형태로 변환한 값
    private static final Pattern PATTERN_SCIENTIFIC_NOTATION = Pattern.compile("^\\d\\.\\d{2}E\\+(\\d{2})$");

    // [단계 4] 품종명의 괄호 문자열 (예: "수박(일반)(꼭지절단)" → "수박"), 중분류명에는 적용하지 않음
    private static final Pattern PATTERN_PARENTHESES = Pattern.compile("\\(.*\\)");

    // [단계 5] 품종명에 한글이 있는지 판별
    private static final Pattern PATTERN_HANGUL = Pattern.compile("[가-힣]");
    // [단계 5] 품종명이 아닌 가공품의 중분류명
    private static final Set<String> PROCESSED_MIDDLE_CATEGORY_NAMES =
            Set.of("곶감", "건고추", "쌀", "차류", "녹차");
    // [단계 5] 규칙으로 걸러지지 않는 가공품 품종명
    private static final Set<String> PROCESSED_VARIETY_NAMES =
            Set.of("반건시", "도건", "화건", "반양건", "깐마늘", "국화차", "홍화차", "호박고지");
    // [단계 5] "건"으로 시작하지만 가공품이 아닌 품종명
    private static final Set<String> NON_PROCESSED_VARIETY_NAMES = Set.of("건시옐로우");
    // [단계 5] 한 글자이지만 식물명으로 기능하는 품종명
    private static final Set<String> SINGLE_SYLLABLE_VARIETY_NAMES = Set.of("감", "얌", "딜");

    /*
     * [단계 6] 하나의 중분류명에 여러 중분류코드가 존재하여 제거하는 중분류코드 (레코드가 가장 많은 중분류코드만 유지)
     * (예: "허브" → 26G6(관엽식물류) 26건 유지, 2802(기타화훼) 2건 제거)
     */
    private static final Set<String> NON_REPRESENTATIVE_MIDDLE_CATEGORY_CODES = Set.of(
            "3606", // 글라디올러스 (대표 2501)
            "0606", // 떫은감 (대표 0712)
            "19I9", // 사과 (대표 0601)
            "1808", // 새싹 (대표 1424)
            "19K8", // 선인장 (대표 2379)
            "0635", // 야자 (대표 2636)
            "2197", // 튜울립 (대표 2537)
            "1605", // 피마자 (대표 19T2)
            "2802"  // 허브 (대표 26G6)
    );

    /*
     * [단계 7] 하나의 중분류코드에 존재하는 여러 중분류명을 하나로 통일하거나 분리하기 위해 설정한 중분류명 (중분류코드 → 중분류명)
     * 1. 같은 식물을 가리키는 중분류명: 하나의 중분류명으로 통일 (예: 0611 → "참다래" 3건, "참다래(키위)" 1건)
     * 2. 포괄 항목인 "기타": 대분류마다 중분류코드가 다르므로 "기타(대분류명)" 형식으로 분리
     */
    private static final Map<String, String> MIDDLE_CATEGORY_NAME_OVERRIDES = Map.ofEntries(
            Map.entry("0611", "참다래"),
            Map.entry("1304", "셀러리"),
            Map.entry("1099", "기타(엽경채류)"),
            Map.entry("1799", "기타(버섯류)"),
            Map.entry("1399", "기타(양채류)"),
            Map.entry("19ZZ", "기타(약용작물류)"),
            Map.entry("0999", "기타(과채류)"),
            Map.entry("1499", "기타(산채류)"),
            Map.entry("1199", "기타(근채류)"),
            Map.entry("1299", "기타(조미채소류)"),
            Map.entry("1699", "기타(특용작물류)"),
            Map.entry("0499", "기타(잡곡류)"),
            Map.entry("0599", "기타(서류)"),
            Map.entry("0699", "기타(과실류)"),
            Map.entry("0399", "기타(두류)"));

    /*
     * [단계 8] 하나의 중분류코드에 서로 다른 식물의 중분류명이 존재하여 부여한 독립 중분류코드 (중분류명 → 중분류코드)
     * - 독립 중분류코드는 같은 대분류 코드 대역에서 원본 CSV가 사용하지 않는 다음 번호 (예: 엽경채류 10xx의 최댓값 1067 다음인 1068)
     * - 소분류코드의 앞 4자리도 독립 중분류코드로 변경 (예: "다채" 106402 → 106802, "카이란"은 1064 유지)
     */
    private static final Map<String, String> INDEPENDENT_MIDDLE_CATEGORY_CODES = Map.of("다채", "1068");

    @Override
    public void migrate(Context context) throws Exception {
        // [준비] Iterator 확보
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

        InputStream is = getClass().getResourceAsStream("/csv/standard_item_code_001.csv");

        MappingIterator<PlantVarietyRow> iterator = csvMapper.readerFor(PlantVarietyRow.class)
                .with(csvSchema)
                .readValues(is);

        List<PlantVarietyRow> csvRows = iterator.readAll();

        // [단계 1~2] 레코드별 코드 형식 복원
        for (PlantVarietyRow row : csvRows) {
            row.recoverBrokenCodes();
            row.padCodes();
        }

        // [단계 3] 중분류코드 - 소분류코드 간 불일치 교정
        Map<String, Set<String>> pairedMiddleCategoryCodesAndNames = new HashMap<>();

        // 중분류코드 - 중분류명 키 - 값 쌍 저장(하나의 중분류코드에 여러 중분류명이 가능함)
        for (PlantVarietyRow row : csvRows) {
            if (row.variety_code.startsWith(row.middle_category_code)) {
                pairedMiddleCategoryCodesAndNames
                        .computeIfAbsent(row.middle_category_code, code -> new HashSet<>())
                        .add(row.middle_category_name);
            }
        }

        // 전채 csv 행에 걸쳐 중분류코드 - 소분류코드 간 불일치 식별 및 수정
        for (PlantVarietyRow row : csvRows) {
            row.correctMismatchWithMiddleCategoryCodeAndVarietyCode(pairedMiddleCategoryCodesAndNames);
        }

        Map<String, PlantVarietyRow> pairedVarietyNamesAndRows = new LinkedHashMap<>();
        for (PlantVarietyRow row : csvRows) {
            // [단계 4] 품종명에서 괄호 문자열 제거
            row.variety_name = PATTERN_PARENTHESES.matcher(row.variety_name).replaceAll("");

            // [단계 5], [단계 6]
            if (row.isNotFunctionalAsVarietyName() || row.hasNonRepresentativeMiddleCategoryCode()) {
                continue;
            }

            // [단계 7]
            row.overrideMiddleCategoryName();

            // [단계 8]
            row.assignIndependentMiddleCategoryCode();

            // [단계 9] 품종명이 중복되는 경우 소분류코드가 사전순으로 가장 작은 레코드만 유지
            pairedVarietyNamesAndRows.merge(row.variety_name, row,
                    (existing, incoming) ->
                            existing.variety_code.compareTo(incoming.variety_code) <= 0 ? existing : incoming);
        }

        insertRows(context, pairedVarietyNamesAndRows.values());
    }

    private void insertRows(Context context, Collection<PlantVarietyRow> rows) throws Exception {
        String sql = """
                INSERT INTO plant_variety (\
                variety_code,
                major_category_name,
                middle_category_code,
                middle_category_name,
                variety_name) VALUES (?, ?, ?, ?, ?);
                """;

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (PlantVarietyRow row : rows) {
                statement.setString(1, row.variety_code);
                statement.setString(2, row.major_category_name);
                statement.setString(3, row.middle_category_code);
                statement.setString(4, row.middle_category_name);
                statement.setString(5, row.variety_name);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static class PlantVarietyRow {
        @JsonProperty("대분류명")
        public String major_category_name;
        @JsonProperty("중분류코드")
        public String middle_category_code;
        @JsonProperty("중분류명(품목명)")
        public String middle_category_name;
        @JsonProperty("소분류코드")
        public String variety_code;
        @JsonProperty("소분류명(품종명)")
        public String variety_name;

        /*
         * [단계 1] 엑셀 지수 표기로 깨진 코드 복원
         * - 소분류코드(4자리 중분류코드): 중분류코드 + "E" + (지수 - 3)
         *      (예: 2403의 "2.40E+06" → "2403E3")
         * - 소분류코드(앞자리 0이 유실된 3자리 중분류코드): "0" + 중분류코드 + "E" + (지수 - 2)
         *      (예: 604의 "6.04E+03" → "0604E1", 같은 중분류의 다른 소분류코드 "0604D9" 형식을 따름)
         */
        private void recoverBrokenCodes() {
            if (PATTERN_SCIENTIFIC_NOTATION.matcher(middle_category_code).matches()) {
                middle_category_code = variety_code.substring(0, MIDDLE_CATEGORY_CODE_LENGTH);
            }
            Matcher varietyCodeMatcher = PATTERN_SCIENTIFIC_NOTATION.matcher(variety_code);
            if (varietyCodeMatcher.matches()) {
                int exponent = Integer.parseInt(varietyCodeMatcher.group(1));
                variety_code =
                        "0".repeat(MIDDLE_CATEGORY_CODE_LENGTH - middle_category_code.length()) +
                                middle_category_code + "E" +
                                (MIDDLE_CATEGORY_CODE_LENGTH - middle_category_code.length() + exponent - 3);
            }
        }

        /*
         * [단계 2] 엑셀이 숫자로만 이루어진 코드를 숫자로 해석하여 유실된 앞자리 0 복원
         * - 중분류코드는 4자리, 소분류코드는 6자리가 되도록 앞에 0을 채움 (예: "604" → "0604", "60401" → "060401")
         */
        private void padCodes() {
            middle_category_code =
                    "0".repeat(MIDDLE_CATEGORY_CODE_LENGTH - middle_category_code.length()) + middle_category_code;
            variety_code = "0".repeat(VARIETY_CODE_LENGTH - variety_code.length()) + variety_code;
        }

        /*
         * [단계 3] 소분류코드의 앞 4자리가 중분류코드와 다른 레코드 교정
         */
        private void correctMismatchWithMiddleCategoryCodeAndVarietyCode(Map<String, Set<String>> pairedMiddleCategoryCodesAndNames) {
            if (variety_code.startsWith(middle_category_code)) {
                return;
            }
            String expectedMiddleCategoryCode = variety_code.substring(0, MIDDLE_CATEGORY_CODE_LENGTH);
            if (pairedMiddleCategoryCodesAndNames.get(expectedMiddleCategoryCode).contains(middle_category_name)) { // 기대되는 중분류코드가 유효할 때
                middle_category_code = expectedMiddleCategoryCode;
            } else { // 기대되는 중분류코드가 유효하지 않아서 기존 중분류코드를 쓰는 게 합리적일 때
                variety_code = middle_category_code + variety_code.substring(MIDDLE_CATEGORY_CODE_LENGTH);
            }
        }

        /*
         * [단계 5] 품종명으로 기능할 수 없는 레코드
         * 1. 포괄 항목: "기타"로 시작하는 품종명, "혼합"을 포함하는 품종명
         * 2. 식물이 아닌 항목: 대분류명 "식물성단미사료"(사료), "캔들"로 시작하는 품종명, "부작"(석부작, 목부작 등)을 포함하는 품종명
         * 3. 가공품: 가공품 중분류명, "건"으로 시작하는 품종명(건조품), "말랭이"를 포함하는 품종명,
         *    "수액" 또는 "종자"로 끝나는 품종명, 그 외 명시한 가공품 품종명
         * 4. 코드형, 빈 값 또는 한 글자 품종명: 한글이 없는 품종명(예: "MK-1", "1350-2", 빈 품종명),
         *    중분류명과 같지 않은 한 글자 품종명(예: "봉", "킹"), 단 식물명으로 기능하는 한 글자 품종명은 유지
         */
        private boolean isNotFunctionalAsVarietyName() {
            return variety_name.startsWith("기타") || variety_name.startsWith("캔들")
                    || (variety_name.startsWith("건") && !NON_PROCESSED_VARIETY_NAMES.contains(variety_name))
                    || variety_name.contains("혼합") || variety_name.contains("부작") || variety_name.contains("말랭이")
                    || variety_name.endsWith("수액") || variety_name.endsWith("종자")
                    || (variety_name.length() == 1 && !variety_name.equals(middle_category_name) && !SINGLE_SYLLABLE_VARIETY_NAMES.contains(variety_name))
                    || !PATTERN_HANGUL.matcher(variety_name).find()
                    || PROCESSED_VARIETY_NAMES.contains(variety_name)
                    || PROCESSED_MIDDLE_CATEGORY_NAMES.contains(middle_category_name)
                    || major_category_name.equals("식물성단미사료");
        }

        // [단계 6] 대표 중분류코드가 아닌 중분류코드의 레코드인지 판별
        private boolean hasNonRepresentativeMiddleCategoryCode() {
            return NON_REPRESENTATIVE_MIDDLE_CATEGORY_CODES.contains(middle_category_code);
        }

        // [단계 7] 중분류코드에 설정된 중분류명으로 통일 및 분리
        private void overrideMiddleCategoryName() {
            middle_category_name = MIDDLE_CATEGORY_NAME_OVERRIDES.getOrDefault(middle_category_code, middle_category_name);
        }

        // [단계 8] 중분류명에 설정된 독립 중분류코드를 중분류코드와 소분류코드 접두어에 부여
        private void assignIndependentMiddleCategoryCode() {
            String code = INDEPENDENT_MIDDLE_CATEGORY_CODES.get(middle_category_name);
            if (code != null) {
                middle_category_code = code;
                variety_code = code + variety_code.substring(MIDDLE_CATEGORY_CODE_LENGTH);
            }
        }
    }
}
