package kr.modusplant.infrastructure.file.persistence.jpa.repository;

import kr.modusplant.infrastructure.file.common.util.entity.PendingFileEntityTestUtils;
import kr.modusplant.infrastructure.file.persistence.jpa.entity.PendingFileEntity;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.infrastructure.file.common.constant.PendingFileConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class PendingFileJpaRepositoryTest implements PendingFileEntityTestUtils {

    private final PendingFileJpaRepository pendingFileRepository;

    @Autowired
    PendingFileJpaRepositoryTest(PendingFileJpaRepository pendingFileRepository) {
        this.pendingFileRepository = pendingFileRepository;
    }

    @DisplayName("createdAt 이전에 생성된 fileKey 조회")
    @Test
    void testFindFileKeysByCreatedAtBefore_givenThreshold_willReturnFileKeys() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().plusSeconds(1);

        // when
        List<String> fileKeys = pendingFileRepository.findFileKeysByCreatedAtBefore(threshold);

        // then
        assertThat(fileKeys).contains(pendingFile.getFileKey());
    }

    @DisplayName("threshold 이후에 생성된 fileKey는 조회되지 않음")
    @Test
    void testFindFileKeysByCreatedAtBefore_givenThreshold_willReturnOnlyFileKeysBeforeThreshold() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().minusSeconds(1);

        // when
        List<String> fileKeys = pendingFileRepository.findFileKeysByCreatedAtBefore(threshold);

        // then
        assertThat(fileKeys).doesNotContain(pendingFile.getFileKey());
    }

    @DisplayName("createdAt 이전에 생성된 레코드 삭제")
    @Test
    void testDeleteByCreatedAtBefore_givenThreshold_willDeletePendingFiles() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().plusSeconds(1);

        // when
        pendingFileRepository.deleteByCreatedAtBefore(threshold);

        // then
        assertThat(pendingFileRepository.existsById(pendingFile.getUlid())).isFalse();
    }

    @DisplayName("threshold 이후에 생성된 레코드는 삭제되지 않음")
    @Test
    void testDeleteByCreatedAtBefore_givenThreshold_willKeepPendingFilesAtOrAfterThreshold() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().minusSeconds(1);

        // when
        pendingFileRepository.deleteByCreatedAtBefore(threshold);

        // then
        assertThat(pendingFileRepository.existsById(pendingFile.getUlid())).isTrue();
    }

    @DisplayName("fileKey 목록으로 레코드 삭제")
    @Test
    void testDeleteByFileKeyIn_givenFileKeys_willDeletePendingFiles() {
        // given
        PendingFileEntity target = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        PendingFileEntity other = pendingFileRepository.saveAndFlush(createPostPendingFileEntity(TEST_MEMBER_PROFILE_FILE_KEY, TEST_MEMBER_DOMAIN));

        // when
        pendingFileRepository.deleteByFileKeyIn(List.of(target.getFileKey()));

        // then
        assertThat(pendingFileRepository.existsById(target.getUlid())).isFalse();
        assertThat(pendingFileRepository.existsById(other.getUlid())).isTrue();
    }

    @DisplayName("대기 파일 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenPendingFileEntity_willReturnRepresentative() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.save(createPostPendingFileEntity());

        // when & then
        assertDoesNotThrow(pendingFile::toString);
    }
}
