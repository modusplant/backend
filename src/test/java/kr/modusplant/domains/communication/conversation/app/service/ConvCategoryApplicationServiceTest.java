package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCategoryRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCategoryResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.mapper.ConvCategoryAppInfraMapper;
import kr.modusplant.domains.communication.conversation.mapper.ConvCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceOnlyContext
class ConvCategoryApplicationServiceTest implements ConvCategoryRequestTestUtils, ConvCategoryResponseTestUtils, ConvCategoryEntityTestUtils {

    private final ConvCategoryApplicationService convCategoryApplicationService;
    private final ConvCategoryRepository convCategoryRepository;
    private final ConvCategoryAppInfraMapper convCategoryAppInfraMapper = new ConvCategoryAppInfraMapperImpl();

    @Autowired
    ConvCategoryApplicationServiceTest(ConvCategoryApplicationService convCategoryApplicationService, ConvCategoryRepository convCategoryRepository) {
        this.convCategoryApplicationService = convCategoryApplicationService;
        this.convCategoryRepository = convCategoryRepository;
    }

    @DisplayName("모든 대화 항목 얻기")
    @Test
    void getAllTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);
        ConvCategoryEntity returnedConvCategoryEntity = createTestConvCategoryEntityWithUuid();

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(returnedConvCategoryEntity);
        given(convCategoryRepository.findAll()).willReturn(List.of(returnedConvCategoryEntity));
        given(convCategoryRepository.existsByCategory(convCategoryEntity.getCategory())).willReturn(false);
        given(convCategoryRepository.existsByOrder(convCategoryEntity.getOrder())).willReturn(false);

        // when
        convCategoryApplicationService.insert(testConvCategoryInsertRequest);

        // then
        assertThat(convCategoryApplicationService.getAll()).isEqualTo(List.of(testConvCategoryResponse));
    }

    @DisplayName("UUID로 대화 항목 얻기")
    @Test
    void getByUuidTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);
        ConvCategoryEntity returnedConvCategoryEntity = createTestConvCategoryEntityWithUuid();

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(returnedConvCategoryEntity);
        given(convCategoryRepository.findByUuid(returnedConvCategoryEntity.getUuid())).willReturn(Optional.of(returnedConvCategoryEntity));
        given(convCategoryRepository.existsByCategory(convCategoryEntity.getCategory())).willReturn(false);
        given(convCategoryRepository.existsByOrder(convCategoryEntity.getOrder())).willReturn(false);

        // when
        convCategoryApplicationService.insert(testConvCategoryInsertRequest);

        // then
        assertThat(convCategoryApplicationService.getByUuid(returnedConvCategoryEntity.getUuid()).orElseThrow()).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("category로 대화 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);
        ConvCategoryEntity returnedConvCategoryEntity = createTestConvCategoryEntityWithUuid();

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(returnedConvCategoryEntity);
        given(convCategoryRepository.existsByCategory(testConvCategoryResponse.category())).willReturn(false);
        given(convCategoryRepository.findByCategory(testConvCategoryResponse.category())).willReturn(Optional.of(returnedConvCategoryEntity));
        given(convCategoryRepository.existsByOrder(convCategoryEntity.getOrder())).willReturn(false);

        // when
        convCategoryApplicationService.insert(testConvCategoryInsertRequest);

        // then
        assertThat(convCategoryApplicationService.getByCategory(returnedConvCategoryEntity.getCategory()).orElseThrow()).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("order로 대화 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);
        ConvCategoryEntity returnedConvCategoryEntity = createTestConvCategoryEntityWithUuid();

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(returnedConvCategoryEntity);
        given(convCategoryRepository.existsByCategory(convCategoryEntity.getCategory())).willReturn(false);
        given(convCategoryRepository.existsByOrder(testConvCategoryResponse.order())).willReturn(false);
        given(convCategoryRepository.findByOrder(testConvCategoryResponse.order())).willReturn(Optional.of(returnedConvCategoryEntity));

        // when
        convCategoryApplicationService.insert(testConvCategoryInsertRequest);

        // then
        assertThat(convCategoryApplicationService.getByOrder(returnedConvCategoryEntity.getOrder()).orElseThrow()).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("빈 대화 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        ConvCategoryEntity convCategoryEntity = createTestConvCategoryEntity();
        UUID uuid = convCategoryEntity.getUuid();
        Integer order = convCategoryEntity.getOrder();
        String category = convCategoryEntity.getCategory();

        // getByUuid
        // given & when
        given(convCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(convCategoryApplicationService.getByUuid(uuid)).isEmpty();

        // getByOrder
        // given & when
        given(convCategoryRepository.findByOrder(order)).willReturn(Optional.empty());

        // then
        assertThat(convCategoryApplicationService.getByOrder(order)).isEmpty();

        // getByCategory
        // given & when
        given(convCategoryRepository.findByCategory(category)).willReturn(Optional.empty());

        // then
        assertThat(convCategoryApplicationService.getByCategory(category)).isEmpty();
    }

    @DisplayName("UUID로 대화 항목 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = testConvCategoryWithUuid.getUuid();
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(convCategoryEntity);
        given(convCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());
        given(convCategoryRepository.existsByUuid(uuid)).willReturn(true);
        given(convCategoryRepository.existsByCategory(convCategoryEntity.getCategory())).willReturn(false);
        given(convCategoryRepository.existsByOrder(convCategoryEntity.getOrder())).willReturn(false);
        willDoNothing().given(convCategoryRepository).deleteByUuid(uuid);

        // when
        convCategoryApplicationService.insert(testConvCategoryInsertRequest);
        convCategoryApplicationService.removeByUuid(uuid);

        // then
        assertThat(convCategoryApplicationService.getByUuid(uuid)).isEmpty();
    }
}