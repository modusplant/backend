package kr.modusplant.domains.group.common.util.entity;

import kr.modusplant.domains.group.common.util.domain.PlantGroupTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;

public interface PlantGroupEntityTestUtils extends PlantGroupTestUtils {
    default PlantGroupEntity createPlantGroupEntity() {
        return PlantGroupEntity.builder()
                .order(plantGroup.getOrder())
                .category(plantGroup.getCategory())
                .build();
    }
}
