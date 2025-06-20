package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.common.context.DomainsServiceWithoutValidationServiceContext;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class QnaCategoryApplicationServiceTest implements QnaCategoryRequestTestUtils, QnaCategoryResponseTestUtils, QnaCategoryEntityTestUtils {

    private final QnaCategoryApplicationService qnaCategoryApplicationService;
    private final QnaCategoryRepository qnaCategoryRepository;
    private final QnaCategoryAppInfraMapper qnaCategoryAppInfraMapper = new QnaCategoryAppInfraMapperImpl();

    @Autowired
    QnaCategoryApplicationServiceTest(QnaCategoryApplicationService qnaCategoryApplicationService, QnaCategoryRepository qnaCategoryRepository) {
        this.qnaCategoryApplicationService = qnaCategoryApplicationService;
        this.qnaCategoryRepository = qnaCategoryRepository;
    }

    @DisplayName("모든 Q&A 항목 얻기")
    @Test
    void getAllTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);
        QnaCategoryEntity returnedQnaCategoryEntity = createTestQnaCategoryEntityWithUuid();

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(returnedQnaCategoryEntity);
        given(qnaCategoryRepository.findAll()).willReturn(List.of(returnedQnaCategoryEntity));
        given(qnaCategoryRepository.existsByCategory(qnaCategoryEntity.getCategory())).willReturn(false);
        given(qnaCategoryRepository.existsByOrder(qnaCategoryEntity.getOrder())).willReturn(false);

        // when
        qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);

        // then
        assertThat(qnaCategoryApplicationService.getAll()).isEqualTo(List.of(testQnaCategoryResponse));
    }

    @DisplayName("UUID로 Q&A 항목 얻기")
    @Test
    void getByUuidTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);
        QnaCategoryEntity returnedQnaCategoryEntity = createTestQnaCategoryEntityWithUuid();

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(returnedQnaCategoryEntity);
        given(qnaCategoryRepository.findByUuid(returnedQnaCategoryEntity.getUuid())).willReturn(Optional.of(returnedQnaCategoryEntity));
        given(qnaCategoryRepository.existsByCategory(qnaCategoryEntity.getCategory())).willReturn(false);
        given(qnaCategoryRepository.existsByOrder(qnaCategoryEntity.getOrder())).willReturn(false);

        // when
        qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);

        // then
        assertThat(qnaCategoryApplicationService.getByUuid(returnedQnaCategoryEntity.getUuid()).orElseThrow()).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("category로 Q&A 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);
        QnaCategoryEntity returnedQnaCategoryEntity = createTestQnaCategoryEntityWithUuid();

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(returnedQnaCategoryEntity);
        given(qnaCategoryRepository.existsByCategory(testQnaCategoryResponse.category())).willReturn(false);
        given(qnaCategoryRepository.findByCategory(testQnaCategoryResponse.category())).willReturn(Optional.of(returnedQnaCategoryEntity));
        given(qnaCategoryRepository.existsByOrder(qnaCategoryEntity.getOrder())).willReturn(false);

        // when
        qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);

        // then
        assertThat(qnaCategoryApplicationService.getByCategory(returnedQnaCategoryEntity.getCategory()).orElseThrow()).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("order로 Q&A 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);
        QnaCategoryEntity returnedQnaCategoryEntity = createTestQnaCategoryEntityWithUuid();

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(returnedQnaCategoryEntity);
        given(qnaCategoryRepository.existsByCategory(qnaCategoryEntity.getCategory())).willReturn(false);
        given(qnaCategoryRepository.existsByOrder(testQnaCategoryResponse.order())).willReturn(false);
        given(qnaCategoryRepository.findByOrder(testQnaCategoryResponse.order())).willReturn(Optional.of(returnedQnaCategoryEntity));

        // when
        qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);

        // then
        assertThat(qnaCategoryApplicationService.getByOrder(returnedQnaCategoryEntity.getOrder()).orElseThrow()).isEqualTo(testQnaCategoryResponse);
    }

    @DisplayName("빈 Q&A 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = createTestQnaCategoryEntity();
        UUID uuid = qnaCategoryEntity.getUuid();
        Integer order = qnaCategoryEntity.getOrder();
        String category = qnaCategoryEntity.getCategory();

        // getByUuid
        // given & when
        given(qnaCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(qnaCategoryApplicationService.getByUuid(uuid)).isEmpty();

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

    @DisplayName("UUID로 Q&A 항목 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = testQnaCategoryWithUuid.getUuid();
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryAppInfraMapper.toQnaCategoryEntity(testQnaCategoryInsertRequest);

        given(qnaCategoryRepository.save(qnaCategoryEntity)).willReturn(qnaCategoryEntity);
        given(qnaCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());
        given(qnaCategoryRepository.existsByUuid(uuid)).willReturn(true);
        given(qnaCategoryRepository.existsByCategory(qnaCategoryEntity.getCategory())).willReturn(false);
        given(qnaCategoryRepository.existsByOrder(qnaCategoryEntity.getOrder())).willReturn(false);
        willDoNothing().given(qnaCategoryRepository).deleteByUuid(uuid);

        // when
        qnaCategoryApplicationService.insert(testQnaCategoryInsertRequest);
        qnaCategoryApplicationService.removeByUuid(uuid);

        // then
        assertThat(qnaCategoryApplicationService.getByUuid(uuid)).isEmpty();
    }
}