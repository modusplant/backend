package kr.modusplant.domains.group.persistence.repository;

import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryOnlyContext
class PlantGroupRepositoryTest implements PlantGroupEntityTestUtils {

    private final PlantGroupRepository plantGroupRepository;

    @Autowired
    PlantGroupRepositoryTest(PlantGroupRepository plantGroupRepository) {
        this.plantGroupRepository = plantGroupRepository;
    }

    @Test
    @DisplayName("order로 식물 그룹 찾기")
    void findByOrderTest() {
        // given
        PlantGroupEntity plantGroup = createPlantGroupEntity();

        // when
        plantGroupRepository.save(plantGroup);

        // then
        assertThat(plantGroupRepository.findByOrder(plantGroup.getOrder()).orElseThrow()).isEqualTo(plantGroup);
    }

    @Test
    @DisplayName("category로 식물 그룹 찾기")
    void findByCategoryTest() {
        // given
        PlantGroupEntity plantGroup = createPlantGroupEntity();

        // when
        plantGroupRepository.save(plantGroup);

        // then
        assertThat(plantGroupRepository.findByCategory(plantGroup.getCategory()).orElseThrow()).isEqualTo(plantGroup);
    }

    @Test
    @DisplayName("createdAt으로 식물 그룹 찾기")
    void findByCreatedAtTest() {
        // given
        PlantGroupEntity plantGroup = createPlantGroupEntity();

        // when
        PlantGroupEntity savedPlantGroup = plantGroupRepository.save(plantGroup);

        // then
        assertThat(plantGroupRepository.findByCreatedAt(savedPlantGroup.getCreatedAt()).getFirst()).isEqualTo(plantGroup);
    }
}