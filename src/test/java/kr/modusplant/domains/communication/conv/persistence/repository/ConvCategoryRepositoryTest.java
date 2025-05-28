package kr.modusplant.domains.communication.conv.persistence.repository;

import kr.modusplant.domains.communication.conv.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class ConvCategoryRepositoryTest implements ConvCategoryEntityTestUtils {

    private final ConvCategoryRepository convCategoryRepository;

    @Autowired
    ConvCategoryRepositoryTest(ConvCategoryRepository convCategoryRepository) {
        this.convCategoryRepository = convCategoryRepository;
    }

    @DisplayName("order으로 팁 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        convCategoryRepository.save(convCategoryEntity);

        // then
        assertThat(convCategoryRepository.findByOrder(convCategory.getOrder()).orElseThrow()).isEqualTo(convCategoryEntity);
    }

    @DisplayName("category로 팁 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        convCategoryRepository.save(convCategoryEntity);

        // then
        assertThat(convCategoryRepository.findByCategory(convCategory.getCategory()).orElseThrow()).isEqualTo(convCategoryEntity);
    }

    @DisplayName("createdAt으로 팁 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        ConvCategoryEntity convCategory = convCategoryRepository.save(convCategoryEntity);

        // then
        assertThat(convCategoryRepository.findByCreatedAt(convCategory.getCreatedAt()).getFirst()).isEqualTo(convCategory);
    }

    @DisplayName("order로 팁 항목 삭제")
    @Test
    void deleteByOrderTest() {
        // given
        ConvCategoryEntity convCategory = convCategoryRepository.save(convCategoryEntity);
        Integer order = convCategory.getOrder();

        // when
        convCategoryRepository.deleteByOrder(order);

        // then
        assertThat(convCategoryRepository.findByOrder(order)).isEmpty();
    }

    @DisplayName("order로 팁 항목 확인")
    @Test
    void existsByOrderTest() {
        // given & when
        ConvCategoryEntity convCategory = convCategoryRepository.save(convCategoryEntity);

        // then
        assertThat(convCategoryRepository.existsByOrder(convCategory.getOrder())).isEqualTo(true);
    }
}