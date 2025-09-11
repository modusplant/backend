package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class CommSecondaryCategoryRepositoryTest implements CommSecondaryCategoryEntityTestUtils {

    private final CommSecondaryCategoryRepository commCategoryRepository;

    @Autowired
    CommSecondaryCategoryRepositoryTest(CommSecondaryCategoryRepository commCategoryRepository) {
        this.commCategoryRepository = commCategoryRepository;
    }

    @DisplayName("UUID로 컨텐츠 항목 찾기")
    @Test
    void findByUuidTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());

        // then
        assertThat(commCategoryRepository.findByUuid(entity.getUuid()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("category로 컨텐츠 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());

        // then
        assertThat(commCategoryRepository.findByCategory(entity.getCategory()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("order로 컨텐츠 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());

        // then
        assertThat(commCategoryRepository.findByOrder(entity.getOrder()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("createdAt으로 컨텐츠 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());

        // then
        assertThat(commCategoryRepository.findByCreatedAt(entity.getCreatedAt()).getFirst()).isEqualTo(entity);
    }

    @DisplayName("UUID로 컨텐츠 항목 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());
        UUID uuid = entity.getUuid();

        // when
        commCategoryRepository.deleteByUuid(uuid);

        // then
        assertThat(commCategoryRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("UUID로 컨텐츠 항목 확인")
    @Test
    void existsByUuidTest() {
        // given & when
        CommSecondaryCategoryEntity entity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());

        // then
        assertThat(commCategoryRepository.existsByUuid(entity.getUuid())).isEqualTo(true);
    }
}