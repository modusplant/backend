package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCategoryRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCategoryResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConvCategoryAppInfraMapperTest implements ConvCategoryRequestTestUtils, ConvCategoryResponseTestUtils, ConvCategoryEntityTestUtils {

    private final ConvCategoryAppInfraMapper convCategoryAppInfraMapper = new ConvCategoryAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toConvCategoryResponseTest() {
        assertThat(convCategoryAppInfraMapper.toConvCategoryResponse(testConvCategoryEntity)).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toConvCategoryEntityTest() {
        assertThat(convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest).getOrder()).isEqualTo(testConvCategory.getOrder());
    }
}