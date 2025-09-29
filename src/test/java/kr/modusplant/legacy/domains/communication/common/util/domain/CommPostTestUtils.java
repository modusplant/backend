package kr.modusplant.legacy.domains.communication.common.util.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.infrastructure.persistence.generator.UlidIdGenerator;
import kr.modusplant.legacy.domains.communication.domain.model.CommPost;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberConstant;
import org.hibernate.generator.EventType;

import java.io.IOException;
import java.io.UncheckedIOException;

public interface CommPostTestUtils extends CommPrimaryCategoryTestUtils, CommSecondaryCategoryTestUtils, SiteMemberConstant {
    ObjectMapper objectMapper = new ObjectMapper();
    UlidIdGenerator generator = new UlidIdGenerator();

    CommPost TEST_COMM_POST = CommPost.builder()
            .likeCount(0)
            .viewCount(0L)
            .title("물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드")
            .content(createSampleContent())
            .build();

    CommPost TEST_COMM_POST_WITH_ULID = CommPost.builder()
            .ulid(generator.generate(null, null,null, EventType.INSERT))
            .primaryCategoryUuid(TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getUuid())
            .secondaryCategoryUuid(TEST_COMM_SECONDARY_CATEGORY_WITH_UUID.getUuid())
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .likeCount(TEST_COMM_POST.getLikeCount())
            .viewCount(TEST_COMM_POST.getViewCount())
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
