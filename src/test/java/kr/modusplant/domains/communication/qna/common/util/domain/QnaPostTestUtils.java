package kr.modusplant.domains.communication.qna.common.util.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.communication.qna.domain.model.QnaPost;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.global.persistence.generator.UlidIdGenerator;
import org.hibernate.generator.EventType;

import java.io.IOException;
import java.io.UncheckedIOException;

public interface QnaPostTestUtils extends QnaCategoryTestUtils, SiteMemberTestUtils {
    ObjectMapper objectMapper = new ObjectMapper();
    UlidIdGenerator generator = new UlidIdGenerator();

    QnaPost qnaPost = QnaPost.builder()
            .title("물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드")
            .content(createSampleContent())
            .build();

    QnaPost qnaPostWithUlid = QnaPost.builder()
            .ulid(generator.generate(null, null,null, EventType.INSERT))
            .groupOrder(testQnaCategory.getOrder())
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .title("물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드")
            .content(createSampleContent())
            .build();

    static JsonNode createSampleContent() {
        String json = "[\n" +
                "  {\n" +
                "    \"type\": \"text\",\n" +
                "    \"filename\": \"text_0.txt\",\n" +
                "    \"order\": 1,\n" +
                "    \"data\": \"Hello, this is text part 1.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"image\",\n" +
                "    \"filename\": \"image_0.jpg\",\n" +
                "    \"order\": 2,\n" +
                "    \"src\": \"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"text\",\n" +
                "    \"filename\": \"text_1.txt\",\n" +
                "    \"order\": 3,\n" +
                "    \"value\": \"This is text part 2.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"video\",\n" +
                "    \"filename\": \"video_0.mp4\",\n" +
                "    \"order\": 4,\n" +
                "    \"src\": \"/videos/2a7b8c9d-12e3-45f6-789a-bcde0123f456.mp4\"\n" +
                "  }\n" +
                "]";

        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException("Invalid JSON content for test entity", e);
        }
    }
}
