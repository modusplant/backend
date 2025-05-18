package kr.modusplant.domains.tip.common.util.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.group.common.util.domain.PlantGroupTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.tip.domain.model.TipPost;
import kr.modusplant.global.persistence.generator.ULIDGenerator;

import java.io.IOException;
import java.io.UncheckedIOException;

public interface TipPostTestUtils extends PlantGroupTestUtils, SiteMemberTestUtils {
    ObjectMapper objectMapper = new ObjectMapper();
    ULIDGenerator generator = new ULIDGenerator();

    TipPost tipPost = TipPost.builder()
            .title("물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드")
            .content(createSampleContent())
            .build();

    TipPost tipPostWithUlid = TipPost.builder()
            .ulid((String) generator.generate(null, null))
            .groupOrder(plantGroup.getOrder())
            .authMemberUuid(memberBasicUserWithUuid.getUuid())
            .createMemberUuid(memberBasicUserWithUuid.getUuid())
            .title("물 주는 타이밍, 이제 헷갈리지 마세요! 식물별 물 주기 가이드")
            .content(createSampleContent())
            .build();

    static JsonNode createSampleContent() {
        String json = "[\n" +
                "  {\n" +
                "    \"type\": \"text\",\n" +
                "    \"order\": 0,\n" +
                "    \"value\": \"Hello, this is text part 1.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"image\",\n" +
                "    \"order\": 1,\n" +
                "    \"src\": \"/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"text\",\n" +
                "    \"order\": 2,\n" +
                "    \"value\": \"This is text part 2.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"video\",\n" +
                "    \"order\": 3,\n" +
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
