package kr.modusplant.legacy.domains.communication.mapper;

import kr.modusplant.framework.out.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCategoryRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ORDER;
import static org.assertj.core.api.Assertions.assertThat;

class CommSecondaryCategoryAppInfraMapperTest implements CommCategoryRequestTestUtils, CommCategoryResponseTestUtils, CommSecondaryCategoryEntityTestUtils {

    private final CommSecondaryCategoryAppInfraMapper commCategoryAppInfraMapper = new CommSecondaryCategoryAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toCommCategoryResponseTest() {
        assertThat(commCategoryAppInfraMapper.toCommCategoryResponse(createTestCommSecondaryCategoryEntityWithUuid())).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toCommCategoryEntityTest() {
        assertThat(commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST).getOrder()).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_ORDER);
    }
}