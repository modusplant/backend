package kr.modusplant.infrastructure.file.service;

import kr.modusplant.infrastructure.file.persistence.jpa.entity.PendingFileEntity;
import kr.modusplant.infrastructure.file.persistence.jpa.repository.PendingFileJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.infrastructure.file.common.constant.PendingFileConstant.TEST_POST_CONTENT_FILE_KEY;
import static kr.modusplant.infrastructure.file.common.constant.PendingFileConstant.TEST_POST_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PendingFileServiceTest {

    @InjectMocks
    private PendingFileService pendingFileService;

    @Mock
    private PendingFileJpaRepository pendingFileJpaRepository;

    @DisplayName("fileKey 목록을 domain과 함께 추적 대상으로 저장함")
    @Test
    void testTrackPendingFiles_givenFileKeys_willSavePendingFileKeysAndDomain() {
        // given
        List<String> fileKeys = List.of(TEST_POST_CONTENT_FILE_KEY, TEST_POST_DOMAIN+"/01KM6B54J6G4DSGHT22NJ8Z1WC/video/video_0_1.png");

        // when
        pendingFileService.trackPendingFiles(fileKeys);

        // then
        ArgumentCaptor<List<PendingFileEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(pendingFileJpaRepository).saveAll(captor.capture());
        List<PendingFileEntity> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getFileKey()).isEqualTo(fileKeys.get(0));
        assertThat(saved.get(0).getDomain()).isEqualTo(TEST_POST_DOMAIN);
        assertThat(saved.get(1).getFileKey()).isEqualTo(fileKeys.get(1));
        assertThat(saved.get(1).getDomain()).isEqualTo(TEST_POST_DOMAIN);
    }

    @DisplayName("fileKey에 구분자가 없으면 fileKey 전체를 domain으로 사용함")
    @Test
    void testTrackPendingFiles_givenFileKeyWithoutSlash_willUseWholeKeyAsDomain() {
        // given
        String filename = "image.png";
        List<String> fileKeys = List.of(filename);

        // when
        pendingFileService.trackPendingFiles(fileKeys);

        // then
        ArgumentCaptor<List<PendingFileEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(pendingFileJpaRepository).saveAll(captor.capture());
        assertThat(captor.getValue().get(0).getDomain()).isEqualTo(filename);
    }

    @DisplayName("fileKey 목록이 존재하면 추적 대상에서 해제함")
    @Test
    void testUntrackPendingFiles_givenNonEmptyList_willDeleteByFileKeyIn() {
        // given
        List<String> fileKeys = List.of(TEST_POST_CONTENT_FILE_KEY);

        // when
        pendingFileService.untrackPendingFiles(fileKeys);

        // then
        verify(pendingFileJpaRepository).deleteByFileKeyIn(fileKeys);
    }

    @DisplayName("fileKey 목록이 null이면 리포지토리를 호출하지 않음")
    @Test
    void testUntrackPendingFiles_givenNull_willNotCallRepository() {
        // when
        pendingFileService.untrackPendingFiles(null);

        // then
        verify(pendingFileJpaRepository, never()).deleteByFileKeyIn(any());
    }

    @DisplayName("fileKey 목록이 비어있으면 바로 return함")
    @Test
    void testUntrackPendingFiles_givenEmptyFileKeyList_willDoNothing() {
        // when
        pendingFileService.untrackPendingFiles(List.of());

        // then
        verify(pendingFileJpaRepository, never()).deleteByFileKeyIn(any());
    }

    @DisplayName("threshold 이전에 생성된 fileKey 목록 조회를 리포지토리에 위임함")
    @Test
    void testFindExpiredFileKeys_givenThreshold_willReturnFileKeys() {
        // given
        LocalDateTime threshold = LocalDateTime.now();
        given(pendingFileJpaRepository.findFileKeysByCreatedAtBefore(threshold)).willReturn(List.of(TEST_POST_CONTENT_FILE_KEY));

        // when
        List<String> result = pendingFileService.findExpiredFileKeys(threshold);

        // then
        assertThat(result).containsExactly(TEST_POST_CONTENT_FILE_KEY);
        verify(pendingFileJpaRepository).findFileKeysByCreatedAtBefore(threshold);
    }

    @DisplayName("threshold 이전에 생성된 레코드 삭제를 리포지토리에 위임함")
    @Test
    void testDeleteExpiredRecords_givenThreshold_willDelegateToRepository() {
        // given
        LocalDateTime threshold = LocalDateTime.now();

        // when
        pendingFileService.deleteExpiredRecords(threshold);

        // then
        verify(pendingFileJpaRepository).deleteByCreatedAtBefore(threshold);
    }
}
