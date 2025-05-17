package kr.modusplant.domains.group.common.util.domain;

import kr.modusplant.domains.group.domain.model.PlantGroup;

public interface PlantGroupTestUtils {
    PlantGroup plantGroup = PlantGroup.builder()
            .order(1)
            .category("관엽식물")
            .build();

    PlantGroup otherGroup = PlantGroup.builder()
            .order(2)
            .category("기타")
            .build();
}
