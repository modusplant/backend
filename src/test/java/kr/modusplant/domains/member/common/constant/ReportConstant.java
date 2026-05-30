package kr.modusplant.domains.member.common.constant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.JSONB;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportConstant {
    private static final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();
    private static final ObjectMapper mapper = objectMapper();

    public static final String TEST_REPORT_ULID = "01KJ862TB91KP6BK301RZM17K7";
    public static final String TEST_REPORT_TITLE = "보고서 제목";
    public static final String TEST_REPORT_CONTENT = "보고서 컨텐츠";
    public static final Integer TEST_REPORT_IMAGE_NUMBER_3 = 3;
    public static final Integer TEST_REPORT_SIZE = 2;
    public static final LocalDateTime TEST_REPORT_DISMISSED_AT = LocalDateTime.of(2026, 5, 1, 6, 0);
    public static final LocalDateTime TEST_REPORT_CHECKED_AT = LocalDateTime.of(2026, 5, 1, 0, 0);
    public static final LocalDateTime TEST_REPORT_CREATED_AT = LocalDateTime.of(2026, 4, 30, 0, 0);
    public static final String TEST_REPORT_IMAGE_FILE_NAME_1 = "image_0.png";
    public static final String TEST_REPORT_IMAGE_FILE_NAME_2 = "image_1.png";
    public static final String TEST_REPORT_IMAGE_FILE_NAME_3 = "image_2.png";
    public static final String TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1 =
            "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME_1;
    public static final String TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2 =
            "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME_2;
    public static final String TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3 =
            "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME_3;
    public static final List<String> TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS =
            List.of(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3);
    public static final byte[] TEST_REPORT_IMAGE_BYTES_1 = "Image 1 for report".getBytes();
    public static final byte[] TEST_REPORT_IMAGE_BYTES_2 = "Image 2 for report".getBytes();
    public static final byte[] TEST_REPORT_IMAGE_BYTES_3 = "Image 3 for report".getBytes();
    public static final List<byte[]> TEST_REPORT_IMAGE_BYTES_LIST =
            List.of(TEST_REPORT_IMAGE_BYTES_1, TEST_REPORT_IMAGE_BYTES_2, TEST_REPORT_IMAGE_BYTES_3);
    public static final List<MultipartFile> TEST_REPORT_IMAGES =
            List.of(new MockMultipartFile(
                            "image", TEST_REPORT_IMAGE_FILE_NAME_1, "image/png", TEST_REPORT_IMAGE_BYTES_1),
                    new MockMultipartFile(
                            "image", TEST_REPORT_IMAGE_FILE_NAME_2, "image/png", TEST_REPORT_IMAGE_BYTES_2),
                    new MockMultipartFile(
                            "image", TEST_REPORT_IMAGE_FILE_NAME_3, "image/png", TEST_REPORT_IMAGE_BYTES_3));
    public static final String TEST_REPORT_IMAGE_JSON =
            String.format("""
                            [
                                {
                                    "filename": "%s",
                                    "src": "%s"
                                },
                                {
                                    "filename": "%s",
                                    "src": "%s"
                                },
                                {
                                    "filename": "%s",
                                    "src": "%s"
                                }
                            ]
                            """,
                    TEST_REPORT_IMAGE_FILE_NAME_1, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1,
                    TEST_REPORT_IMAGE_FILE_NAME_2, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2,
                    TEST_REPORT_IMAGE_FILE_NAME_3, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3);
    public static final JsonNode TEST_REPORT_IMAGE_JSON_NODE;
    public static final JSONB TEST_REPORT_IMAGE_JSONB;

    static {
        try {
            TEST_REPORT_IMAGE_JSON_NODE = mapper.readValue(TEST_REPORT_IMAGE_JSON, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        TEST_REPORT_IMAGE_JSONB = jsonbJsonNodeConverter.to(TEST_REPORT_IMAGE_JSON_NODE);
    }
}