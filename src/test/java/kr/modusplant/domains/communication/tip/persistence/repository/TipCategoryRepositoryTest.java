package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TipCategoryRepositoryTest implements TipCategoryEntityTestUtils {

    private final TipCategoryRepository tipCategoryRepository;

    @Autowired
    TipCategoryRepositoryTest(TipCategoryRepository tipCategoryRepository) {
        this.tipCategoryRepository = tipCategoryRepository;
    }

    @DisplayName("order으로 팁 항목 찾기")
    @Test
    void findByOrderTest() {
        // given
        TipCategoryEntity tipCategory = createTipCategoryEntity();

        // when
        tipCategoryRepository.save(tipCategory);

        // then
        assertThat(tipCategoryRepository.findByOrder(tipCategory.getOrder()).orElseThrow()).isEqualTo(tipCategory);
    }

    @DisplayName("category로 팁 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given
        TipCategoryEntity tipCategory = createTipCategoryEntity();

        // when
        tipCategoryRepository.save(tipCategory);

        // then
        assertThat(tipCategoryRepository.findByCategory(tipCategory.getCategory()).orElseThrow()).isEqualTo(tipCategory);
    }

    @DisplayName("createdAt으로 팁 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given
        TipCategoryEntity tipCategory = createTipCategoryEntity();

        // when
        tipCategory = tipCategoryRepository.save(tipCategory);

        // then
        assertThat(tipCategoryRepository.findByCreatedAt(tipCategory.getCreatedAt()).getFirst()).isEqualTo(tipCategory);
    }

    @DisplayName("order로 팁 항목 삭제")
    @Test
    void deleteByOrderTest() {
        // given
        TipCategoryEntity tipCategory = tipCategoryRepository.save(createTipCategoryEntity());
        Integer order = tipCategory.getOrder();

        // when
        tipCategoryRepository.deleteByOrder(order);

        // then
        assertThat(tipCategoryRepository.findByOrder(order)).isEmpty();
    }

    @DisplayName("order로 팁 항목 확인")
    @Test
    void existsByOrderTest() {
        // given
        TipCategoryEntity tipCategory = createTipCategoryEntity();

        // when
        tipCategoryRepository.save(tipCategory);

        // then
        assertThat(tipCategoryRepository.existsByOrder(tipCategory.getOrder())).isEqualTo(true);
    }
}