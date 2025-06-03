package kr.modusplant.domains.communication.tip.mapper;

import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipCategoryRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCategoryResponseTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TipCategoryAppInfraMapperTest implements TipCategoryRequestTestUtils, TipCategoryResponseTestUtils, TipCategoryEntityTestUtils {

    private final TipCategoryAppInfraMapper tipCategoryAppInfraMapper = new TipCategoryAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toTipCategoryResponseTest() {
        assertThat(tipCategoryAppInfraMapper.toTipCategoryResponse(createTestTipCategoryEntityWithUuid())).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toTipCategoryEntityTest() {
        assertThat(tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest).getOrder()).isEqualTo(testTipCategory.getOrder());
    }
}