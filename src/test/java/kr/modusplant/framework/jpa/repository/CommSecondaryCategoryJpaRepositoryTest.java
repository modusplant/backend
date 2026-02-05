package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class CommSecondaryCategoryJpaRepositoryTest implements CommSecondaryCategoryEntityTestUtils {

    private final CommSecondaryCategoryJpaRepository commCategoryRepository;

    @Autowired
    CommSecondaryCategoryJpaRepositoryTest(CommSecondaryCategoryJpaRepository commCategoryRepository) {
        this.commCategoryRepository = commCategoryRepository;
    }

    @DisplayName("카테고리 id로 컨텐츠 항목 찾기")
    @Test
    void findByUuidTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(createCommPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findById(entity.getId()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("category로 컨텐츠 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(createCommPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findByCategory(entity.getCategory()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("order로 컨텐츠 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(createCommPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findByOrder(entity.getOrder()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("createdAt으로 컨텐츠 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(createCommPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.findByCreatedAt(entity.getCreatedAt()).getFirst()).isEqualTo(entity);
    }

    @DisplayName("UUID로 컨텐츠 항목 확인")
    @Test
    void existsByUuidTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(createCommPrimaryCategoryEntity()).build());

        // then
        assertThat(commCategoryRepository.existsById(entity.getId())).isEqualTo(true);
    }

    @DisplayName("2차 항목 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenCommSecondaryCategoryEntity_willReturnRepresentative() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(createCommPrimaryCategoryEntity()).build());

        // then
        assertDoesNotThrow(entity::toString);
    }
}