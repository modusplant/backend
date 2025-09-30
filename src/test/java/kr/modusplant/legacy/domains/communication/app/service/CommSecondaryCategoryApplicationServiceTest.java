package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCategoryRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import kr.modusplant.legacy.domains.communication.mapper.CommSecondaryCategoryAppInfraMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class CommSecondaryCategoryApplicationServiceTest implements CommCategoryRequestTestUtils, CommCategoryResponseTestUtils, CommSecondaryCategoryEntityTestUtils {

    private final CommSecondaryCategoryApplicationService commCategoryApplicationService;
    private final CommSecondaryCategoryJpaRepository commCategoryRepository;
    private final CommSecondaryCategoryAppInfraMapper commCategoryAppInfraMapper;

    @Autowired
    CommSecondaryCategoryApplicationServiceTest(CommSecondaryCategoryApplicationService commCategoryApplicationService, CommSecondaryCategoryJpaRepository commCategoryRepository, CommSecondaryCategoryAppInfraMapper commCategoryAppInfraMapper) {
        this.commCategoryApplicationService = commCategoryApplicationService;
        this.commCategoryRepository = commCategoryRepository;
        this.commCategoryAppInfraMapper = commCategoryAppInfraMapper;
    }

    @DisplayName("모든 컨텐츠 2차 항목 얻기")
    @Test
    void getAllTest() {
        // given
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);
        CommSecondaryCategoryEntity returnedCommSecondaryCategoryEntity = createTestCommSecondaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commSecondaryCategoryEntity)).willReturn(returnedCommSecondaryCategoryEntity);
        given(commCategoryRepository.findAll()).willReturn(List.of(returnedCommSecondaryCategoryEntity));
        given(commCategoryRepository.existsByCategory(commSecondaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(commSecondaryCategoryEntity.getOrder())).willReturn(false);

        // when
        commCategoryApplicationService.insert(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getAll()).isEqualTo(List.of(TEST_COMM_SECONDARY_CATEGORY_RESPONSE));
    }

    @DisplayName("UUID로 컨텐츠 2차 항목 얻기")
    @Test
    void getByUuidTest() {
        // given
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);
        CommSecondaryCategoryEntity returnedCommSecondaryCategoryEntity = createTestCommSecondaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commSecondaryCategoryEntity)).willReturn(returnedCommSecondaryCategoryEntity);
        given(commCategoryRepository.findByUuid(returnedCommSecondaryCategoryEntity.getUuid())).willReturn(Optional.of(returnedCommSecondaryCategoryEntity));
        given(commCategoryRepository.existsByCategory(commSecondaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(commSecondaryCategoryEntity.getOrder())).willReturn(false);

        // when
        commCategoryApplicationService.insert(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getByUuid(returnedCommSecondaryCategoryEntity.getUuid()).orElseThrow()).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("category로 컨텐츠 2차 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);
        CommSecondaryCategoryEntity returnedCommSecondaryCategoryEntity = createTestCommSecondaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commSecondaryCategoryEntity)).willReturn(returnedCommSecondaryCategoryEntity);
        given(commCategoryRepository.existsByCategory(TEST_COMM_SECONDARY_CATEGORY_RESPONSE.category())).willReturn(false);
        given(commCategoryRepository.findByCategory(TEST_COMM_SECONDARY_CATEGORY_RESPONSE.category())).willReturn(Optional.of(returnedCommSecondaryCategoryEntity));
        given(commCategoryRepository.existsByOrder(commSecondaryCategoryEntity.getOrder())).willReturn(false);

        // when
        commCategoryApplicationService.insert(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getByCategory(returnedCommSecondaryCategoryEntity.getCategory()).orElseThrow()).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("order로 컨텐츠 2차 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);
        CommSecondaryCategoryEntity returnedCommSecondaryCategoryEntity = createTestCommSecondaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commSecondaryCategoryEntity)).willReturn(returnedCommSecondaryCategoryEntity);
        given(commCategoryRepository.existsByCategory(commSecondaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(TEST_COMM_SECONDARY_CATEGORY_RESPONSE.order())).willReturn(false);
        given(commCategoryRepository.findByOrder(TEST_COMM_SECONDARY_CATEGORY_RESPONSE.order())).willReturn(Optional.of(returnedCommSecondaryCategoryEntity));

        // when
        commCategoryApplicationService.insert(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getByOrder(returnedCommSecondaryCategoryEntity.getOrder()).orElseThrow()).isEqualTo(TEST_COMM_SECONDARY_CATEGORY_RESPONSE);
    }

    @DisplayName("빈 컨텐츠 2차 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = createTestCommSecondaryCategoryEntity();
        UUID uuid = commSecondaryCategoryEntity.getUuid();
        Integer order = commSecondaryCategoryEntity.getOrder();
        String category = commSecondaryCategoryEntity.getCategory();

        // getByUuid
        // given & when
        given(commCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(commCategoryApplicationService.getByUuid(uuid)).isEmpty();

        // getByOrder
        // given & when
        given(commCategoryRepository.findByOrder(order)).willReturn(Optional.empty());

        // then
        assertThat(commCategoryApplicationService.getByOrder(order)).isEmpty();

        // getByCategory
        // given & when
        given(commCategoryRepository.findByCategory(category)).willReturn(Optional.empty());

        // then
        assertThat(commCategoryApplicationService.getByCategory(category)).isEmpty();
    }

    @DisplayName("UUID로 컨텐츠 2차 항목 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = TEST_COMM_SECONDARY_CATEGORY_UUID;
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);

        given(commCategoryRepository.save(commSecondaryCategoryEntity)).willReturn(commSecondaryCategoryEntity);
        given(commCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());
        given(commCategoryRepository.existsByUuid(uuid)).willReturn(true);
        given(commCategoryRepository.existsByCategory(commSecondaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(commSecondaryCategoryEntity.getOrder())).willReturn(false);
        willDoNothing().given(commCategoryRepository).deleteByUuid(uuid);

        // when
        commCategoryApplicationService.insert(TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST);
        commCategoryApplicationService.removeByUuid(uuid);

        // then
        assertThat(commCategoryApplicationService.getByUuid(uuid)).isEmpty();
    }
}