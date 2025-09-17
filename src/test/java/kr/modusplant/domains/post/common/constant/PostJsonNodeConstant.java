package kr.modusplant.domains.post.common.constant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostJsonNodeConstant {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final JsonNode TEST_POST_CONTENT = createTestPostContent();

    private static JsonNode createTestPostContent() {
        try {
            String json = "[{\"type\":\"text\",\"filename\":\"text_0.txt\",\"order\":1,\"data\":\"Hello, this is text part 1.\"}," +
                    "{\"type\":\"image\",\"filename\":\"image_0.jpg\",\"order\":2,\"src\":\"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"}," +
                    "{\"type\":\"text\",\"filename\":\"text_1.txt\",\"order\":3,\"value\":\"This is text part 2.\"}," +
                    "{\"type\":\"video\",\"filename\":\"video_0.mp4\",\"order\":4,\"src\":\"/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4\"}]";
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test post content", e);
        }
    }

}
