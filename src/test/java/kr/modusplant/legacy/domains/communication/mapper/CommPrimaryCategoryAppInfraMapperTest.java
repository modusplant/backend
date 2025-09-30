package kr.modusplant.legacy.domains.communication.mapper;

import kr.modusplant.framework.out.jpa.entity.common.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCategoryRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ORDER;
import static org.assertj.core.api.Assertions.assertThat;

class CommPrimaryCategoryAppInfraMapperTest implements CommCategoryRequestTestUtils, CommCategoryResponseTestUtils, CommPrimaryCategoryEntityTestUtils {

    private final CommPrimaryCategoryAppInfraMapper commCategoryAppInfraMapper = new CommPrimaryCategoryAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toCommCategoryResponseTest() {
        assertThat(commCategoryAppInfraMapper.toCommCategoryResponse(createTestCommPrimaryCategoryEntityWithUuid())).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toCommCategoryEntityTest() {
        assertThat(commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST).getOrder()).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_ORDER);
    }
}