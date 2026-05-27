package kr.modusplant.domains.post.framework.outbound.jpa.repository;

import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.common.util.SecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.SecondaryCategoryJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class SecondaryCategoryJpaRepositoryTest implements SecondaryCategoryEntityTestUtils {

    private final SecondaryCategoryJpaRepository commCategoryRepository;

    @Autowired
    SecondaryCategoryJpaRepositoryTest(SecondaryCategoryJpaRepository commCategoryRepository) {
        this.commCategoryRepository = commCategoryRepository;
    }

    @DisplayName("카테고리 id로 컨텐츠 항목 찾기")
    @Test
    void findByUuidTest() {
        // given & when
        SecondaryCategoryEntity entity = commCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(createPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findById(entity.getId()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("category로 컨텐츠 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        SecondaryCategoryEntity entity = commCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(createPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findByCategory(entity.getCategory()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("order로 컨텐츠 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        SecondaryCategoryEntity entity = commCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(createPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findByOrder(entity.getOrder()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("createdAt으로 컨텐츠 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        SecondaryCategoryEntity entity = commCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(createPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findByCreatedAt(entity.getCreatedAt()).getFirst()).isEqualTo(entity);
    }

    @DisplayName("UUID로 컨텐츠 항목 확인")
    @Test
    void existsByUuidTest() {
        // given & when
        SecondaryCategoryEntity entity = commCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(createPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.existsById(entity.getId())).isEqualTo(true);
    }

    @DisplayName("2차 항목 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenCommSecondaryCategoryEntity_willReturnRepresentative() {
        // given & when
        SecondaryCategoryEntity entity = commCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(createPrimaryCategoryEntity()).build());

        // then
        assertDoesNotThrow(entity::toString);
    }
}