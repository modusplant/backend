package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @DisplayName("모든 팁 항목 얻기")
    @Test
    void getAllTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);
        TipCategoryEntity returnedTipCategoryEntity = createTestTipCategoryEntityWithUuid();

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(returnedTipCategoryEntity);
        given(tipCategoryRepository.findAll()).willReturn(List.of(returnedTipCategoryEntity));
        given(tipCategoryRepository.existsByCategory(tipCategoryEntity.getCategory())).willReturn(false);
        given(tipCategoryRepository.existsByOrder(tipCategoryEntity.getOrder())).willReturn(false);

        // when
        tipCategoryApplicationService.insert(testTipCategoryInsertRequest);

        // then
        assertThat(tipCategoryApplicationService.getAll()).isEqualTo(List.of(testTipCategoryResponse));
    }

    @DisplayName("UUID로 팁 항목 얻기")
    @Test
    void getByUuidTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);
        TipCategoryEntity returnedTipCategoryEntity = createTestTipCategoryEntityWithUuid();

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(returnedTipCategoryEntity);
        given(tipCategoryRepository.findByUuid(returnedTipCategoryEntity.getUuid())).willReturn(Optional.of(returnedTipCategoryEntity));
        given(tipCategoryRepository.existsByCategory(tipCategoryEntity.getCategory())).willReturn(false);
        given(tipCategoryRepository.existsByOrder(tipCategoryEntity.getOrder())).willReturn(false);

        // when
        tipCategoryApplicationService.insert(testTipCategoryInsertRequest);

        // then
        assertThat(tipCategoryApplicationService.getByUuid(returnedTipCategoryEntity.getUuid()).orElseThrow()).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("category로 팁 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);
        TipCategoryEntity returnedTipCategoryEntity = createTestTipCategoryEntityWithUuid();

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(returnedTipCategoryEntity);
        given(tipCategoryRepository.existsByCategory(testTipCategoryResponse.category())).willReturn(false);
        given(tipCategoryRepository.findByCategory(testTipCategoryResponse.category())).willReturn(Optional.of(returnedTipCategoryEntity));
        given(tipCategoryRepository.existsByOrder(tipCategoryEntity.getOrder())).willReturn(false);

        // when
        tipCategoryApplicationService.insert(testTipCategoryInsertRequest);

        // then
        assertThat(tipCategoryApplicationService.getByCategory(returnedTipCategoryEntity.getCategory()).orElseThrow()).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("order로 팁 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);
        TipCategoryEntity returnedTipCategoryEntity = createTestTipCategoryEntityWithUuid();

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(returnedTipCategoryEntity);
        given(tipCategoryRepository.existsByCategory(tipCategoryEntity.getCategory())).willReturn(false);
        given(tipCategoryRepository.existsByOrder(testTipCategoryResponse.order())).willReturn(false);
        given(tipCategoryRepository.findByOrder(testTipCategoryResponse.order())).willReturn(Optional.of(returnedTipCategoryEntity));

        // when
        tipCategoryApplicationService.insert(testTipCategoryInsertRequest);

        // then
        assertThat(tipCategoryApplicationService.getByOrder(returnedTipCategoryEntity.getOrder()).orElseThrow()).isEqualTo(testTipCategoryResponse);
    }

    @DisplayName("빈 팁 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        TipCategoryEntity tipCategoryEntity = createTestTipCategoryEntity();
        UUID uuid = tipCategoryEntity.getUuid();
        Integer order = tipCategoryEntity.getOrder();
        String category = tipCategoryEntity.getCategory();

        // getByUuid
        // given & when
        given(tipCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(tipCategoryApplicationService.getByUuid(uuid)).isEmpty();

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

    @DisplayName("UUID로 팁 항목 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = testTipCategoryWithUuid.getUuid();
        TipCategoryEntity tipCategoryEntity = tipCategoryAppInfraMapper.toTipCategoryEntity(testTipCategoryInsertRequest);

        given(tipCategoryRepository.save(tipCategoryEntity)).willReturn(tipCategoryEntity);
        given(tipCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());
        given(tipCategoryRepository.existsByUuid(uuid)).willReturn(true);
        given(tipCategoryRepository.existsByCategory(tipCategoryEntity.getCategory())).willReturn(false);
        given(tipCategoryRepository.existsByOrder(tipCategoryEntity.getOrder())).willReturn(false);
        willDoNothing().given(tipCategoryRepository).deleteByUuid(uuid);

        // when
        tipCategoryApplicationService.insert(testTipCategoryInsertRequest);
        tipCategoryApplicationService.removeByUuid(uuid);

        // then
        assertThat(tipCategoryApplicationService.getByUuid(uuid)).isEmpty();
    }
}