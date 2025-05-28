package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipCategoryRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCategoryResponseTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.mapper.TipCategoryAppInfraMapper;
import kr.modusplant.domains.communication.tip.mapper.TipCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceOnlyContext
class TipCategoryApplicationServiceTest implements TipCategoryRequestTestUtils, TipCategoryResponseTestUtils, TipCategoryEntityTestUtils {

    private final TipCategoryApplicationService tipCategoryApplicationService;
    private final TipCategoryRepository tipCategoryRepository;
    private final TipCategoryAppInfraMapper tipCategoryAppInfraMapper = new TipCategoryAppInfraMapperImpl();

    @Autowired
    TipCategoryApplicationServiceTest(TipCategoryApplicationService tipCategoryApplicationService, TipCategoryRepository tipCategoryRepository) {
        this.tipCategoryApplicationService = tipCategoryApplicationService;
        this.tipCategoryRepository = tipCategoryRepository;
    }

    @DisplayName("order로 팁 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);
        TipCategoryEntity returnedTipCategoryEntity = testTipCategoryEntity;

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(returnedTipCategoryEntity);
        given(tipCategoryRepository.findByOrder(testTipCategoryResponse.order())).willReturn(Optional.of(returnedTipCategoryEntity));

        // when
        TipCategoryResponse testTipCategoryResponse = tipCategoryApplicationService.insert(testTipCategoryInsertRequest);

        // then
        assertThat(tipCategoryApplicationService.getByOrder(testTipCategoryResponse.order()).orElseThrow()).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("category로 팁 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);
        TipCategoryEntity returnedTipCategoryEntity = testTipCategoryEntity;

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(returnedTipCategoryEntity);
        given(tipCategoryRepository.findByCategory(testTipCategoryResponse.category())).willReturn(Optional.empty()).willReturn(Optional.of(returnedTipCategoryEntity));

        // when
        TipCategoryResponse testTipCategoryResponse = tipCategoryApplicationService.insert(testTipCategoryInsertRequest);

        // then
        assertThat(tipCategoryApplicationService.getByCategory(testTipCategoryResponse.category()).orElseThrow()).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("빈 팁 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        TipCategoryEntity tipCategoryEntity = testTipCategoryEntity;
        Integer order = tipCategoryEntity.getOrder();
        String category = tipCategoryEntity.getCategory();

        // getByOrder
        // given & when
        given(tipCategoryRepository.findByOrder(order)).willReturn(Optional.empty());

        // then
        assertThat(tipCategoryApplicationService.getByOrder(order)).isEmpty();

        // getByCategory
        // given & when
        given(tipCategoryRepository.findByCategory(category)).willReturn(Optional.empty());

        // then
        assertThat(tipCategoryApplicationService.getByCategory(category)).isEmpty();
    }

    @DisplayName("order로 팁 항목 제거")
    @Test
    void removeByOrderTest() {
        // given
        Integer order = testTipCategory.getOrder();
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(tipCategoryEntity);
        given(tipCategoryRepository.findByOrder(order)).willReturn(Optional.of(tipCategoryEntity)).willReturn(Optional.empty());
        given(tipCategoryRepository.findByCategory(tipCategoryEntity.getCategory())).willReturn(Optional.empty());
        willDoNothing().given(tipCategoryRepository).deleteByOrder(order);

        // when
        tipCategoryApplicationService.insert(testTipCategoryInsertRequest);
        tipCategoryApplicationService.removeByOrder(order);

        // then
        assertThat(tipCategoryApplicationService.getByOrder(order)).isEmpty();
    }
}