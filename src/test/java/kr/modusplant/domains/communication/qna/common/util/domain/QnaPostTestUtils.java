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
