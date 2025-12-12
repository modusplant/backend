package kr.modusplant.infrastructure.swear.common.util;

import kr.modusplant.infrastructure.swear.enums.SwearType;
import kr.modusplant.infrastructure.swear.persistence.jpa.entity.SwearEntity;

import java.util.List;

public interface SwearEntityTestUtils {
    SwearEntity testSwearEntity = SwearEntity.builder()
            .word("병신").type(SwearType.GENERAL).build();

    List<SwearEntity> testSwearEntityList = List.of(
            SwearEntity.builder().word("병신").type(SwearType.GENERAL).build(),
            SwearEntity.builder().word("애미").type(SwearType.FAMILY).build(),
            SwearEntity.builder().word("성인").type(SwearType.SEXUAL).build()
    );
}
