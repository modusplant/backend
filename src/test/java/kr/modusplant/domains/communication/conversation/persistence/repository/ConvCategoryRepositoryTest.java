package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class ConvCategoryRepositoryTest implements ConvCategoryEntityTestUtils {

    private final ConvCategoryRepository convCategoryRepository;

    @Autowired
    ConvCategoryRepositoryTest(ConvCategoryRepository convCategoryRepository) {
        this.convCategoryRepository = convCategoryRepository;
    }

    @DisplayName("UUID로 대화 항목 찾기")
    @Test
    void findByUuidTest() {
        // given & when
        ConvCategoryEntity entity = convCategoryRepository.save(createTestConvCategoryEntity());

        // then
        assertThat(convCategoryRepository.findByUuid(entity.getUuid()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("category로 대화 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        ConvCategoryEntity entity = convCategoryRepository.save(createTestConvCategoryEntity());

        // then
        assertThat(convCategoryRepository.findByCategory(entity.getCategory()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("order로 대화 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        ConvCategoryEntity entity = convCategoryRepository.save(createTestConvCategoryEntity());

        // then
        assertThat(convCategoryRepository.findByOrder(entity.getOrder()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("createdAt으로 대화 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        ConvCategoryEntity entity = convCategoryRepository.save(createTestConvCategoryEntity());

        // then
        assertThat(convCategoryRepository.findByCreatedAt(entity.getCreatedAt()).getFirst()).isEqualTo(entity);
    }

    @DisplayName("UUID로 대화 항목 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        ConvCategoryEntity entity = convCategoryRepository.save(createTestConvCategoryEntity());
        UUID uuid = entity.getUuid();

        // when
        convCategoryRepository.deleteByUuid(uuid);

        // then
        assertThat(convCategoryRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("UUID로 대화 항목 확인")
    @Test
    void existsByUuidTest() {
        // given & when
        ConvCategoryEntity entity = convCategoryRepository.save(createTestConvCategoryEntity());

        // then
        assertThat(convCategoryRepository.existsByUuid(entity.getUuid())).isEqualTo(true);
    }
}