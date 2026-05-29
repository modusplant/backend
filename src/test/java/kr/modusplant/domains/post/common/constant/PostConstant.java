package kr.modusplant.domains.post.common.constant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID_STRING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostConstant {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String TEST_POST_ULID = "01K6DH4YCJMS3NJ4JCY8TPXP4T";
    public static final String TEST_POST_ULID2 = "71K59D7R5ZT51X9HVZXGK4A6WN";
    public static final String TEST_AUTHOR_ID_STRING = MEMBER_BASIC_USER_UUID_STRING;
    public static final String TEST_INVALID_POST_ULID = "01ARZ3NDEKTSV4RRFFQ69G5FA";
    public static final String TEST_INVALID_POST_ULID2 = "01ARZ3NDEKTSV4RRFFQ69G5F@V";
    public static final String TEST_POST_TITLE = "물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드";
    public static final String TEST_POST_CONTENT_TEXT = "Hello, this is text part 1. This is text part 2.";
    public static final String[] TEST_POST_ULID_ARRAY = new String[]{"01K6DH4YCJMS3NJ4JCY8TPXP4T"};
    public static final String TEST_POST_CONTENT_IMAGE_FILE_KEY = "/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg";
    public static final List<String> TEST_POST_CONTENT_IMAGE_FILE_KEYS = List.of(TEST_POST_CONTENT_IMAGE_FILE_KEY);

    public static final Integer TEST_POST_LIKE_COUNT = 8;
    public static final Long TEST_POST_VIEW_COUNT = 5L;
    public static final LocalDateTime TEST_POST_CREATED_AT = LocalDateTime.of(2026, 4, 2, 0, 0);
    public static final LocalDateTime TEST_POST_ARCHIVED_AT = LocalDateTime.of(2026, 4, 4, 0, 0);
    public static final LocalDateTime TEST_POST_UPDATED_AT = LocalDateTime.of(2026, 4, 3, 12, 0);
    public static final LocalDateTime TEST_POST_PUBLISHED_AT = LocalDateTime.of(2026, 4, 3, 0, 0);
    public static final JsonNode TEST_POST_CONTENT_JSON_NODE = createSampleContentAsJsonNode();
    public static final String TEST_POST_CONTENT = """
            [
              {
                "type": "text",
                "filename": "text_0.txt",
                "order": 1,
                "data": "Hello, this is text part 1."
              },
              {
                "type": "image",
                "filename": "image_0.jpg",
                "order": 2,
                "src": "/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg"
              },
              {
                "type": "text",
                "filename": "text_1.txt",
                "order": 3,
                "value": "This is text part 2."
              },
              {
                "type": "video",
                "filename": "video_0.mp4",
                "order": 4,
                "src": "/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4"
              }
            ]""";

    private static JsonNode createSampleContentAsJsonNode() {
        try {
            return objectMapper.readTree(TEST_POST_CONTENT);
        } catch (IOException e) {
            throw new UncheckedIOException("Invalid JSON content for test entity", e);
        }
    }
}
