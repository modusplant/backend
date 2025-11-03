package kr.modusplant.shared.persistence.common.constant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommPostConstant {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String TEST_COMM_POST_ULID = "01K6DH4YCJMS3NJ4JCY8TPXP4T";
    public static final UUID TEST_COMM_POST_PRIMARY_CATEGORY_UUID = CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_UUID;
    public static final UUID TEST_COMM_POST_SECONDARY_CATEGORY_UUID = TEST_COMM_SECONDARY_CATEGORY_UUID;
    public static final UUID TEST_COMM_POST_AUTH_MEMBER_UUID = SiteMemberConstant.MEMBER_BASIC_USER_UUID;
    public static final UUID TEST_COMM_POST_CREATE_MEMBER_UUID = SiteMemberConstant.MEMBER_BASIC_USER_UUID;
    public static final Integer TEST_COMM_POST_LIKE_COUNT = 0;
    public static final Long TEST_COMM_POST_VIEW_COUNT = 0L;
    public static final String TEST_COMM_POST_TITLE = "물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드";
    public static final JsonNode TEST_COMM_POST_CONTENT = createSampleContent();
    public static final Boolean TEST_COMM_POST_IS_PUBLISHED = true;

    private static JsonNode createSampleContent() {
        String json = """
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

        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException("Invalid JSON content for test entity", e);
        }
    }
}
