package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TipCategoryRepositoryTest implements TipCategoryEntityTestUtils {

    private final TipCategoryRepository tipCategoryRepository;

    @Autowired
    TipCategoryRepositoryTest(TipCategoryRepository tipCategoryRepository) {
        this.tipCategoryRepository = tipCategoryRepository;
    }

    @DisplayName("UUID로 팁 항목 찾기")
    @Test
    void findByUuidTest() {
        // given & when
        TipCategoryEntity entity = tipCategoryRepository.save(createTestTipCategoryEntity());

        // then
        assertThat(tipCategoryRepository.findByUuid(entity.getUuid()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("category로 팁 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        TipCategoryEntity entity = tipCategoryRepository.save(createTestTipCategoryEntity());

        // then
        assertThat(tipCategoryRepository.findByCategory(entity.getCategory()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("order로 팁 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        TipCategoryEntity entity = tipCategoryRepository.save(createTestTipCategoryEntity());

        // then
        assertThat(tipCategoryRepository.findByOrder(entity.getOrder()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("createdAt으로 팁 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        TipCategoryEntity entity = tipCategoryRepository.save(createTestTipCategoryEntity());

        // then
        assertThat(tipCategoryRepository.findByCreatedAt(entity.getCreatedAt()).getFirst()).isEqualTo(entity);
    }

    @DisplayName("UUID로 팁 항목 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        TipCategoryEntity entity = tipCategoryRepository.save(createTestTipCategoryEntity());
        UUID uuid = entity.getUuid();

        // when
        tipCategoryRepository.deleteByUuid(uuid);

        // then
        assertThat(tipCategoryRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("UUID로 팁 항목 확인")
    @Test
    void existsByUuidTest() {
        // given & when
        TipCategoryEntity entity = tipCategoryRepository.save(createTestTipCategoryEntity());

        // then
        assertThat(tipCategoryRepository.existsByUuid(entity.getUuid())).isEqualTo(true);
    }
}