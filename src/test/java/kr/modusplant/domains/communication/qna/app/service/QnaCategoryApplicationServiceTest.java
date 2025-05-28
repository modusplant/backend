package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCategoryResponse;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaCategoryRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCategoryResponseTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.mapper.QnaCategoryAppInfraMapper;
import kr.modusplant.domains.communication.qna.mapper.QnaCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceOnlyContext
class QnaCategoryApplicationServiceTest implements QnaCategoryRequestTestUtils, QnaCategoryResponseTestUtils, QnaCategoryEntityTestUtils {

    private final QnaCategoryApplicationService qnaCategoryApplicationService;
    private final QnaCategoryRepository qnaCategoryRepository;
    private final QnaCategoryAppInfraMapper qnaCategoryAppInfraMapper = new QnaCategoryAppInfraMapperImpl();

    @Autowired
    QnaCategoryApplicationServiceTest(QnaCategoryApplicationService qnaCategoryApplicationService, QnaCategoryRepository qnaCategoryRepository) {
        this.qnaCategoryApplicationService = qnaCategoryApplicationService;
        this.qnaCategoryRepository = qnaCategoryRepository;
    }

    @DisplayName("order로 Q&A 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);
        QnaCategoryEntity returnedQnaCategoryEntity = testQnaCategoryEntity;

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(returnedQnaCategoryEntity);
        given(qnaCategoryRepository.findByOrder(testQnaCategoryResponse.order())).willReturn(Optional.of(returnedQnaCategoryEntity));

        // when
        QnaCategoryResponse testQnaCategoryResponse = qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);

        // then
        assertThat(qnaCategoryApplicationService.getByOrder(testQnaCategoryResponse.order()).orElseThrow()).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("category로 Q&A 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);
        QnaCategoryEntity returnedQnaCategoryEntity = testQnaCategoryEntity;

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(returnedQnaCategoryEntity);
        given(qnaCategoryRepository.findByCategory(testQnaCategoryResponse.category())).willReturn(Optional.empty()).willReturn(Optional.of(returnedQnaCategoryEntity));

        // when
        QnaCategoryResponse testQnaCategoryResponse = qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);

        // then
        assertThat(qnaCategoryApplicationService.getByCategory(testQnaCategoryResponse.category()).orElseThrow()).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("빈 Q&A 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = testQnaCategoryEntity;
        Integer order = qnaCategoryEntity.getOrder();
        String category = qnaCategoryEntity.getCategory();

        // getByOrder
        // given & when
        given(qnaCategoryRepository.findByOrder(order)).willReturn(Optional.empty());

        // then
        assertThat(qnaCategoryApplicationService.getByOrder(order)).isEmpty();

        // getByCategory
        // given & when
        given(qnaCategoryRepository.findByCategory(category)).willReturn(Optional.empty());

        // then
        assertThat(qnaCategoryApplicationService.getByCategory(category)).isEmpty();
    }

    @DisplayName("order로 Q&A 항목 제거")
    @Test
    void removeByOrderTest() {
        // given
        Integer order = testQnaCategory.getOrder();
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(qnaCategoryEntity);
        given(qnaCategoryRepository.findByOrder(order)).willReturn(Optional.of(qnaCategoryEntity)).willReturn(Optional.empty());
        given(qnaCategoryRepository.findByCategory(qnaCategoryEntity.getCategory())).willReturn(Optional.empty());
        willDoNothing().given(qnaCategoryRepository).deleteByOrder(order);

        // when
        qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);
        qnaCategoryApplicationService.removeByOrder(order);

        // then
        assertThat(qnaCategoryApplicationService.getByOrder(order)).isEmpty();
    }
}