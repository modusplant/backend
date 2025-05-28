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
        // given & when
        tipCategoryRepository.save(testTipCategoryEntity);

        // then
        assertThat(tipCategoryRepository.findByOrder(testTipCategory.getOrder()).orElseThrow()).isEqualTo(testTipCategoryEntity);
    }

    @DisplayName("category로 팁 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        tipCategoryRepository.save(testTipCategoryEntity);

        // then
        assertThat(tipCategoryRepository.findByCategory(testTipCategory.getCategory()).orElseThrow()).isEqualTo(testTipCategoryEntity);
    }

    @DisplayName("createdAt으로 팁 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        TipCategoryEntity tipCategory = tipCategoryRepository.save(testTipCategoryEntity);

        // then
        assertThat(tipCategoryRepository.findByCreatedAt(tipCategory.getCreatedAt()).getFirst()).isEqualTo(tipCategory);
    }

    @DisplayName("order로 팁 항목 삭제")
    @Test
    void deleteByOrderTest() {
        // given
        TipCategoryEntity tipCategory = tipCategoryRepository.save(testTipCategoryEntity);
        Integer order = tipCategory.getOrder();

        // when
        tipCategoryRepository.deleteByOrder(order);

        // then
        assertThat(tipCategoryRepository.findByOrder(order)).isEmpty();
    }

    @DisplayName("order로 팁 항목 확인")
    @Test
    void existsByOrderTest() {
        // given & when
        TipCategoryEntity tipCategory = tipCategoryRepository.save(testTipCategoryEntity);

        // then
        assertThat(tipCategoryRepository.existsByOrder(tipCategory.getOrder())).isEqualTo(true);
    }
}