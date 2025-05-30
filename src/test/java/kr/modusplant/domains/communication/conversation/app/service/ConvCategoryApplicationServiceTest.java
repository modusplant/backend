package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCategoryRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCategoryResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;
import kr.modusplant.domains.communication.conversation.mapper.ConvCategoryAppInfraMapper;
import kr.modusplant.domains.communication.conversation.mapper.ConvCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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

    @DisplayName("order로 대화 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);
        ConvCategoryEntity returnedConvCategoryEntity = testConvCategoryEntity;

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(returnedConvCategoryEntity);
        given(convCategoryRepository.findByOrder(testConvCategoryResponse.order())).willReturn(Optional.of(returnedConvCategoryEntity));

        // when
        ConvCategoryResponse testConvCategoryResponse = convCategoryApplicationService.insert(testConvCategoryInsertRequest);

        // then
        assertThat(convCategoryApplicationService.getByOrder(testConvCategoryResponse.order()).orElseThrow()).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("category로 대화 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);
        ConvCategoryEntity returnedConvCategoryEntity = testConvCategoryEntity;

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(returnedConvCategoryEntity);
        given(convCategoryRepository.findByCategory(testConvCategoryResponse.category())).willReturn(Optional.empty()).willReturn(Optional.of(returnedConvCategoryEntity));

        // when
        ConvCategoryResponse testConvCategoryResponse = convCategoryApplicationService.insert(testConvCategoryInsertRequest);

        // then
        assertThat(convCategoryApplicationService.getByCategory(testConvCategoryResponse.category()).orElseThrow()).isEqualTo(testConvCategoryResponse);
    }

    @DisplayName("빈 대화 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        ConvCategoryEntity convCategoryEntity = testConvCategoryEntity;
        Integer order = convCategoryEntity.getOrder();
        String category = convCategoryEntity.getCategory();

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

    @DisplayName("order로 대화 항목 제거")
    @Test
    void removeByOrderTest() {
        // given
        Integer order = testConvCategory.getOrder();
        ConvCategoryEntity convCategoryEntity = convCategoryAppInfraMapper.toConvCategoryEntity(testConvCategoryInsertRequest);

        given(convCategoryRepository.save(convCategoryEntity)).willReturn(convCategoryEntity);
        given(convCategoryRepository.findByOrder(order)).willReturn(Optional.of(convCategoryEntity)).willReturn(Optional.empty());
        given(convCategoryRepository.findByCategory(convCategoryEntity.getCategory())).willReturn(Optional.empty());
        willDoNothing().given(convCategoryRepository).deleteByOrder(order);

        // when
        convCategoryApplicationService.insert(testConvCategoryInsertRequest);
        convCategoryApplicationService.removeByOrder(order);

        // then
        assertThat(convCategoryApplicationService.getByOrder(order)).isEmpty();
    }
}