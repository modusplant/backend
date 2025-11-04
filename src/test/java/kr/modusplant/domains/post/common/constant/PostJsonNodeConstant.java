package kr.modusplant.domains.post.common.constant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostJsonNodeConstant {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // JSON 문자열 상수
    private static final String JSON_FULL_CONTENT =
            "[{\"type\":\"text\",\"filename\":\"text_0.txt\",\"order\":1,\"data\":\"Hello, this is text part 1.\"}," +
            "{\"type\":\"image\",\"filename\":\"image_0.jpg\",\"order\":2,\"src\":\"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"}," +
            "{\"type\":\"text\",\"filename\":\"text_1.txt\",\"order\":3,\"value\":\"This is text part 2.\"}," +
            "{\"type\":\"video\",\"filename\":\"video_0.mp4\",\"order\":4,\"src\":\"/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4\"}]";
    private static final String JSON_TEXT_AND_IMAGE =
            "[{\"type\":\"text\",\"filename\":\"text_0.txt\",\"order\":1,\"data\":\"First text\"}," +
            "{\"type\":\"image\",\"filename\":\"image_0.jpg\",\"order\":2,\"src\":\"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"}," +
            "{\"type\":\"text\",\"filename\":\"text_1.txt\",\"order\":3,\"data\":\"Second text\"}]";
    private static final String JSON_TEXT_AND_VIDEO =
            "[{\"type\":\"text\",\"filename\":\"text_0.txt\",\"order\":1,\"data\":\"First text\"}," +
            "{\"type\":\"text\",\"filename\":\"text_1.txt\",\"order\":2,\"data\":\"Second text\"}," +
            "{\"type\":\"video\",\"filename\":\"video_0.mp4\",\"order\":3,\"src\":\"/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4\"}]";

    private static final String JSON_IMAGE_AND_VIDEO =
            "[{\"type\":\"video\",\"filename\":\"video_0.mp4\",\"order\":1,\"src\":\"/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4\"}," +
            "{\"type\":\"image\",\"filename\":\"image_0.jpg\",\"order\":2,\"src\":\"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"}," +
            "{\"type\":\"image\",\"filename\":\"image_1.jpg\",\"order\":3,\"src\":\"/images/59h16f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"}]";

    private static final String JSON_VIDEO_AND_FILE =
            "[{\"type\":\"video\",\"filename\":\"video_0.mp4\",\"order\":1,\"src\":\"/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4\"}," +
            "{\"type\":\"application\",\"filename\":\"file_0.pdf\",\"order\":3,\"src\":\"/files/59h16f67-5abc-48d2-95a1-9cb4e78c7890.pdf\"}]";

    private static final String JSON_PREVIEW =
            "[{\"type\":\"text\",\"filename\":\"text_0.txt\",\"order\":1,\"data\":\"First text\"}," +
            "{\"type\":\"image\",\"filename\":\"image_0.jpg\",\"order\":2,\"src\":\"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"}]";

    private static final String JSON_BINARY_DATA = "[{\"type\":\"text\",\"filename\":\"text_0.txt\",\"order\":1,\"data\":\"First text\"}," +
            "{\"type\":\"image\",\"filename\":\"image_0.jpg\",\"order\":2,\"data\":\"iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==\"}]";


    // JsonNode 상수
    public static final JsonNode TEST_POST_CONTENT = createTestPostContent(JSON_FULL_CONTENT);
    public static final JsonNode TEST_POST_CONTENT_TEXT_AND_IMAGE = createTestPostContent(JSON_TEXT_AND_IMAGE);
    public static final JsonNode TEST_POST_CONTENT_TEXT_AND_VIDEO = createTestPostContent(JSON_TEXT_AND_VIDEO);
    public static final JsonNode TEST_POST_CONTENT_IMAGE_AND_VIDEO = createTestPostContent(JSON_IMAGE_AND_VIDEO);
    public static final JsonNode TEST_POST_CONTENT_VIDEO_AND_FILE = createTestPostContent(JSON_VIDEO_AND_FILE);
    public static final JsonNode TEST_POST_CONTENT_PREVIEW = createTestPostContent(JSON_PREVIEW);
    public static final JsonNode TEST_POST_CONTENT_BINARY_DATA = createTestPostContent(JSON_BINARY_DATA);

    private static JsonNode createTestPostContent(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test post content", e);
        }
    }
}
