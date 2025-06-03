package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaCategoryRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCategoryResponseTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QnaCategoryAppInfraMapperTest implements QnaCategoryRequestTestUtils, QnaCategoryResponseTestUtils, QnaCategoryEntityTestUtils {

    private final QnaCategoryAppInfraMapper qnaCategoryAppInfraMapper = new QnaCategoryAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toQnaCategoryResponseTest() {
        assertThat(qnaCategoryAppInfraMapper.toQnaCategoryResponse(createTestQnaCategoryEntityWithUuid())).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toQnaCategoryEntityTest() {
        assertThat(qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest).getOrder()).isEqualTo(testQnaCategory.getOrder());
    }
}