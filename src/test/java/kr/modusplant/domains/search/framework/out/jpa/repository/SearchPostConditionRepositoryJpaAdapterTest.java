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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SearchPostConditionRepositoryJpaAdapterTest implements CommSecondaryCategoryEntityTestUtils {

    private final CommPrimaryCategoryJpaRepository primaryCategoryJpaRepository = mock(CommPrimaryCategoryJpaRepository.class);
    private final CommSecondaryCategoryJpaRepository secondaryCategoryJpaRepository = mock(CommSecondaryCategoryJpaRepository.class);
    private final SearchPostConditionRepositoryJpaAdapter searchPostConditionRepositoryJpaAdapter = new SearchPostConditionRepositoryJpaAdapter(primaryCategoryJpaRepository, secondaryCategoryJpaRepository);

    @Test
    @DisplayName("isIdExist - primaryCategoryId가 존재할 경우 true를 반환한다")
    void isIdExist_Success() {
        // given
        given(primaryCategoryJpaRepository.existsById(TEST_COMM_PRIMARY_CATEGORY_ID)).willReturn(true);

        // when
        boolean result = searchPostConditionRepositoryJpaAdapter.isIdExist(TEST_COMM_PRIMARY_CATEGORY_ID);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isIdsExist - 주어진 primaryCategoryId 하위에 요청한 secondaryCategoryIds가 모두 존재할 경우 true를 반환한다")
    void isIdsExist_Success() {
        // given
        List<Integer> targetSecondaryCategoryIds = List.of(10, 20);

        // 1. Fixture를 활용한 실제 Primary Category 엔티티 생성
        CommPrimaryCategoryEntity primaryCategory = createCommPrimaryCategoryEntityWithId();

        // 2. Fixture와 primaryCategory() 빌더 메서드를 활용한 Secondary Category 엔티티 생성
        // containsAll 검증을 위해 ID를 각기 다르게 오버라이딩 처리
        CommSecondaryCategoryEntity secondaryCategory1 = createCommSecondaryCategoryEntityBuilderWithId()
                .id(10)
                .primaryCategory(primaryCategory)
                .build();

        CommSecondaryCategoryEntity secondaryCategory2 = createCommSecondaryCategoryEntityBuilderWithId()
                .id(20)
                .primaryCategory(primaryCategory)
                .build();

        given(primaryCategoryJpaRepository.findById(TEST_COMM_PRIMARY_CATEGORY_ID))
                .willReturn(Optional.of(primaryCategory));

        given(secondaryCategoryJpaRepository.findByPrimaryCategoryOrderByOrderAsc(primaryCategory))
                .willReturn(List.of(secondaryCategory1, secondaryCategory2));

        // when
        boolean result = searchPostConditionRepositoryJpaAdapter.isIdsExist(TEST_COMM_PRIMARY_CATEGORY_ID, targetSecondaryCategoryIds);

        // then
        assertThat(result).isTrue();
    }
}