package kr.modusplant.domains.search.framework.out.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.jpa.repository.CommSecondaryCategoryJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SearchPostConditionRepositoryJpaAdapterTest implements CommSecondaryCategoryEntityTestUtils {

    private final CommPrimaryCategoryJpaRepository primaryCategoryJpaRepository = mock(CommPrimaryCategoryJpaRepository.class);
    private final CommSecondaryCategoryJpaRepository secondaryCategoryJpaRepository = mock(CommSecondaryCategoryJpaRepository.class);
    private final SearchPostConditionRepositoryJpaAdapter searchPostConditionRepositoryJpaAdapter = new SearchPostConditionRepositoryJpaAdapter(primaryCategoryJpaRepository, secondaryCategoryJpaRepository);

    @Test
    @DisplayName("primaryCategoryId가 존재할 경우 true를 반환")
    void testIsIdExist_givenExistedPrimaryCategoryId_willReturnTrue() {
        // given
        given(primaryCategoryJpaRepository.existsById(TEST_COMM_PRIMARY_CATEGORY_ID)).willReturn(true);

        // when
        boolean result = searchPostConditionRepositoryJpaAdapter.isIdExist(TEST_COMM_PRIMARY_CATEGORY_ID);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("primaryCategoryId가 존재하지 않을 경우 false를 반환")
    void testIsIdExist_givenNotFoundPrimaryCategoryId_willReturnFalse() {
        // given
        given(primaryCategoryJpaRepository.existsById(TEST_COMM_PRIMARY_CATEGORY_ID)).willReturn(false);

        // when
        boolean result = searchPostConditionRepositoryJpaAdapter.isIdExist(TEST_COMM_PRIMARY_CATEGORY_ID);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("primaryCategoryId 하위에 요청한 secondaryCategoryIds가 모두 존재할 경우 true를 반환")
    void testIsIdsExist_givenMatchedSecondaryCategoryIds_willReturnTrue() {
        // given
        CommPrimaryCategoryEntity primaryCategory = createCommPrimaryCategoryEntityWithId();

        CommSecondaryCategoryEntity secondaryCategory1 = createCommSecondaryCategoryEntityBuilderWithId()
                .id(TEST_COMM_SECONDARY_CATEGORY_ID_1)
                .primaryCategory(primaryCategory)
                .build();

        CommSecondaryCategoryEntity secondaryCategory2 = createCommSecondaryCategoryEntityBuilderWithId()
                .id(TEST_COMM_SECONDARY_CATEGORY_ID_2)
                .primaryCategory(primaryCategory)
                .build();

        CommSecondaryCategoryEntity secondaryCategory3 = createCommSecondaryCategoryEntityBuilderWithId()
                .id(TEST_COMM_SECONDARY_CATEGORY_ID_3)
                .primaryCategory(primaryCategory)
                .build();

        given(primaryCategoryJpaRepository.findById(TEST_COMM_PRIMARY_CATEGORY_ID))
                .willReturn(Optional.of(primaryCategory));

        given(secondaryCategoryJpaRepository.findByPrimaryCategoryOrderByOrderAsc(primaryCategory))
                .willReturn(List.of(secondaryCategory1, secondaryCategory2, secondaryCategory3));

        // when
        boolean result = searchPostConditionRepositoryJpaAdapter.isIdsExist(
                TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID);

        // then
        assertThat(result).isTrue();
    }
}