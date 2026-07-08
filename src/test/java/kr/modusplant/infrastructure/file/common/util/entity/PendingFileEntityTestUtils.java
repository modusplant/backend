package kr.modusplant.infrastructure.file.common.util.entity;

import kr.modusplant.infrastructure.file.persistence.jpa.entity.PendingFileEntity;

import static kr.modusplant.infrastructure.file.common.constant.PendingFileConstant.TEST_POST_CONTENT_FILE_KEY;
import static kr.modusplant.infrastructure.file.common.constant.PendingFileConstant.TEST_POST_DOMAIN;

public interface PendingFileEntityTestUtils {
    default PendingFileEntity createPostPendingFileEntity() {
        return PendingFileEntity.builder()
                .fileKey(TEST_POST_CONTENT_FILE_KEY)
                .domain(TEST_POST_DOMAIN)
                .build();
    }

    default PendingFileEntity createPostPendingFileEntity(String fileKey, String domain) {
        return PendingFileEntity.builder()
                .fileKey(fileKey)
                .domain(domain)
                .build();
    }
}
