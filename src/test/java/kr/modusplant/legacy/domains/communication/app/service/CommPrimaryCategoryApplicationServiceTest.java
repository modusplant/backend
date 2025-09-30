package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCategoryRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import kr.modusplant.legacy.domains.communication.mapper.CommPrimaryCategoryAppInfraMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.framework.out.jpa.entity.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class CommPrimaryCategoryApplicationServiceTest implements CommCategoryRequestTestUtils, CommCategoryResponseTestUtils, CommPrimaryCategoryEntityTestUtils {

    private final CommPrimaryCategoryApplicationService commCategoryApplicationService;
    private final CommPrimaryCategoryJpaRepository commCategoryRepository;
    private final CommPrimaryCategoryAppInfraMapper commCategoryAppInfraMapper;

    @Autowired
    CommPrimaryCategoryApplicationServiceTest(CommPrimaryCategoryApplicationService commCategoryApplicationService, CommPrimaryCategoryJpaRepository commCategoryRepository, CommPrimaryCategoryAppInfraMapper commCategoryAppInfraMapper) {
        this.commCategoryApplicationService = commCategoryApplicationService;
        this.commCategoryRepository = commCategoryRepository;
        this.commCategoryAppInfraMapper = commCategoryAppInfraMapper;
    }

    @DisplayName("모든 컨텐츠 1차 항목 얻기")
    @Test
    void getAllTest() {
        // given
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);
        CommPrimaryCategoryEntity returnedCommPrimaryCategoryEntity = createTestCommPrimaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commPrimaryCategoryEntity)).willReturn(returnedCommPrimaryCategoryEntity);
        given(commCategoryRepository.findAll()).willReturn(List.of(returnedCommPrimaryCategoryEntity));
        given(commCategoryRepository.existsByCategory(commPrimaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(commPrimaryCategoryEntity.getOrder())).willReturn(false);

        // when
        commCategoryApplicationService.insert(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getAll()).isEqualTo(List.of(TEST_COMM_PRIMARY_CATEGORY_RESPONSE));
    }

    @DisplayName("UUID로 컨텐츠 1차 항목 얻기")
    @Test
    void getByUuidTest() {
        // given
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);
        CommPrimaryCategoryEntity returnedCommPrimaryCategoryEntity = createTestCommPrimaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commPrimaryCategoryEntity)).willReturn(returnedCommPrimaryCategoryEntity);
        given(commCategoryRepository.findByUuid(returnedCommPrimaryCategoryEntity.getUuid())).willReturn(Optional.of(returnedCommPrimaryCategoryEntity));
        given(commCategoryRepository.existsByCategory(commPrimaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(commPrimaryCategoryEntity.getOrder())).willReturn(false);

        // when
        commCategoryApplicationService.insert(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getByUuid(returnedCommPrimaryCategoryEntity.getUuid()).orElseThrow()).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("category로 컨텐츠 1차 항목 얻기")
    @Test
    void getByNameTest() {
        // given
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);
        CommPrimaryCategoryEntity returnedCommPrimaryCategoryEntity = createTestCommPrimaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commPrimaryCategoryEntity)).willReturn(returnedCommPrimaryCategoryEntity);
        given(commCategoryRepository.existsByCategory(TEST_COMM_PRIMARY_CATEGORY_RESPONSE.category())).willReturn(false);
        given(commCategoryRepository.findByCategory(TEST_COMM_PRIMARY_CATEGORY_RESPONSE.category())).willReturn(Optional.of(returnedCommPrimaryCategoryEntity));
        given(commCategoryRepository.existsByOrder(commPrimaryCategoryEntity.getOrder())).willReturn(false);

        // when
        commCategoryApplicationService.insert(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getByCategory(returnedCommPrimaryCategoryEntity.getCategory()).orElseThrow()).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("order로 컨텐츠 1차 항목 얻기")
    @Test
    void getByOrderTest() {
        // given
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);
        CommPrimaryCategoryEntity returnedCommPrimaryCategoryEntity = createTestCommPrimaryCategoryEntityWithUuid();

        given(commCategoryRepository.save(commPrimaryCategoryEntity)).willReturn(returnedCommPrimaryCategoryEntity);
        given(commCategoryRepository.existsByCategory(commPrimaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(TEST_COMM_PRIMARY_CATEGORY_RESPONSE.order())).willReturn(false);
        given(commCategoryRepository.findByOrder(TEST_COMM_PRIMARY_CATEGORY_RESPONSE.order())).willReturn(Optional.of(returnedCommPrimaryCategoryEntity));

        // when
        commCategoryApplicationService.insert(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);

        // then
        assertThat(commCategoryApplicationService.getByOrder(returnedCommPrimaryCategoryEntity.getOrder()).orElseThrow()).isEqualTo(TEST_COMM_PRIMARY_CATEGORY_RESPONSE);
    }

    @DisplayName("빈 컨텐츠 1차 항목 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = createTestCommPrimaryCategoryEntity();
        UUID uuid = commPrimaryCategoryEntity.getUuid();
        Integer order = commPrimaryCategoryEntity.getOrder();
        String category = commPrimaryCategoryEntity.getCategory();

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

    @DisplayName("UUID로 컨텐츠 1차 항목 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = TEST_COMM_SECONDARY_CATEGORY_UUID;
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = commCategoryAppInfraMapper.toCommCategoryEntity(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);

        given(commCategoryRepository.save(commPrimaryCategoryEntity)).willReturn(commPrimaryCategoryEntity);
        given(commCategoryRepository.findByUuid(uuid)).willReturn(Optional.empty());
        given(commCategoryRepository.existsByUuid(uuid)).willReturn(true);
        given(commCategoryRepository.existsByCategory(commPrimaryCategoryEntity.getCategory())).willReturn(false);
        given(commCategoryRepository.existsByOrder(commPrimaryCategoryEntity.getOrder())).willReturn(false);
        willDoNothing().given(commCategoryRepository).deleteByUuid(uuid);

        // when
        commCategoryApplicationService.insert(TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST);
        commCategoryApplicationService.removeByUuid(uuid);

        // then
        assertThat(commCategoryApplicationService.getByUuid(uuid)).isEmpty();
    }
}