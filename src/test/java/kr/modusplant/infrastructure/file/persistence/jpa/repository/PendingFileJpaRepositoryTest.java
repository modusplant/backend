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

    @DisplayName("생성 시각 이후 threshold로 fileKey 목록 반환")
    @Test
    void testFindFileKeysByCreatedAtBefore_givenThresholdAfterCreatedAt_willReturnList() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().plusSeconds(1);

        // when
        List<String> fileKeys = pendingFileRepository.findFileKeysByCreatedAtBefore(threshold);

        // then
        assertThat(fileKeys).contains(pendingFile.getFileKey());
    }

    @DisplayName("생성 시각 이전 threshold로 빈 목록 반환")
    @Test
    void testFindFileKeysByCreatedAtBefore_givenThresholdBeforeCreatedAt_willReturnList() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().minusSeconds(1);

        // when
        List<String> fileKeys = pendingFileRepository.findFileKeysByCreatedAtBefore(threshold);

        // then
        assertThat(fileKeys).doesNotContain(pendingFile.getFileKey());
    }

    @DisplayName("생성 시각 이후 threshold로 레코드 삭제 활동 수행")
    @Test
    void testDeleteByCreatedAtBefore_givenThresholdAfterCreatedAt_willDeletePendingFiles() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().plusSeconds(1);

        // when
        pendingFileRepository.deleteByCreatedAtBefore(threshold);

        // then
        assertThat(pendingFileRepository.existsById(pendingFile.getUlid())).isFalse();
    }

    @DisplayName("생성 시각 이전 threshold로 레코드 유지 활동 수행")
    @Test
    void testDeleteByCreatedAtBefore_givenThresholdBeforeCreatedAt_willKeepPendingFiles() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        LocalDateTime threshold = pendingFile.getCreatedAt().minusSeconds(1);

        // when
        pendingFileRepository.deleteByCreatedAtBefore(threshold);

        // then
        assertThat(pendingFileRepository.existsById(pendingFile.getUlid())).isTrue();
    }

    @DisplayName("존재하는 fileKey로 목록 반환")
    @Test
    void testFindFileKeysByFileKeyIn_givenExistingFileKeys_willReturnList() {
        // given
        PendingFileEntity target = pendingFileRepository.saveAndFlush(createPostPendingFileEntity());
        PendingFileEntity other = pendingFileRepository.saveAndFlush(createPostPendingFileEntity(TEST_MEMBER_PROFILE_FILE_KEY, TEST_MEMBER_DOMAIN));

        // when
        List<String> fileKeys = pendingFileRepository.findFileKeysByFileKeyIn(List.of(target.getFileKey()));

        // then
        assertThat(fileKeys).containsExactly(target.getFileKey());
        assertThat(fileKeys).doesNotContain(other.getFileKey());
    }

    @DisplayName("존재하지 않는 fileKey로 빈 목록 반환")
    @Test
    void testFindFileKeysByFileKeyIn_givenNonExistingFileKey_willReturnList() {
        // given
        pendingFileRepository.saveAndFlush(createPostPendingFileEntity());

        // when
        List<String> fileKeys = pendingFileRepository.findFileKeysByFileKeyIn(List.of(TEST_MEMBER_PROFILE_FILE_KEY));

        // then
        assertThat(fileKeys).isEmpty();
    }

    @DisplayName("fileKey 목록으로 레코드 삭제 활동 수행")
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

    @DisplayName("대기 파일 엔터티로 문자열 반환")
    @Test
    void testToString_givenPendingFileEntity_willReturnString() {
        // given
        PendingFileEntity pendingFile = pendingFileRepository.save(createPostPendingFileEntity());

        // when & then
        assertDoesNotThrow(pendingFile::toString);
    }
}
