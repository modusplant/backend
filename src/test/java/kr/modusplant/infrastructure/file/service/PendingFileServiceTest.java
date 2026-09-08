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

import static kr.modusplant.infrastructure.file.common.constant.PendingFileConstant.TEST_MEMBER_PROFILE_FILE_KEY;
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

    @DisplayName("fileKey와 domain 저장 활동 수행")
    @Test
    void testTrackPendingFiles_givenFileKeys_willSavePendingFileKeysAndDomain() {
        // given
        List<String> fileKeys = List.of(TEST_POST_CONTENT_FILE_KEY, TEST_POST_DOMAIN + "/01KM6B54J6G4DSGHT22NJ8Z1WC/video/video_0_1.png");

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

    @DisplayName("구분자 없는 fileKey로 도메인 설정 활동 수행")
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
        assertThat(captor.getValue().getFirst().getDomain()).isEqualTo(filename);
    }

    @DisplayName("이미 추적 중인 fileKey 제외 활동 수행")
    @Test
    void testTrackPendingFiles_givenAlreadyTrackedFileKeys_willExcludeFromSave() {
        // given
        String existingFileKey = TEST_POST_CONTENT_FILE_KEY;
        String newFileKey = TEST_MEMBER_PROFILE_FILE_KEY;
        List<String> fileKeys = List.of(existingFileKey, newFileKey);
        given(pendingFileJpaRepository.findFileKeysByFileKeyIn(fileKeys)).willReturn(List.of(existingFileKey));

        // when
        pendingFileService.trackPendingFiles(fileKeys);

        // then
        ArgumentCaptor<List<PendingFileEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(pendingFileJpaRepository).saveAll(captor.capture());
        List<PendingFileEntity> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().getFileKey()).isEqualTo(newFileKey);
    }

    @DisplayName("null 목록으로 리포지토리 미호출")
    @Test
    void testTrackPendingFiles_givenNull_willNotCallRepository() {
        // given & when
        pendingFileService.trackPendingFiles(null);

        // then
        verify(pendingFileJpaRepository, never()).findFileKeysByFileKeyIn(any());
        verify(pendingFileJpaRepository, never()).saveAll(any());
    }

    @DisplayName("빈 목록으로 리포지토리 미호출")
    @Test
    void testTrackPendingFiles_givenEmptyFileKeyList_willNotCallRepository() {
        // given & when
        pendingFileService.trackPendingFiles(List.of());

        // then
        verify(pendingFileJpaRepository, never()).findFileKeysByFileKeyIn(any());
        verify(pendingFileJpaRepository, never()).saveAll(any());
    }

    @DisplayName("fileKey 목록으로 추적 해제 활동 수행")
    @Test
    void testUntrackPendingFiles_givenNonEmptyList_willDeleteByFileKeyIn() {
        // given
        List<String> fileKeys = List.of(TEST_POST_CONTENT_FILE_KEY);
        given(pendingFileJpaRepository.findFileKeysByFileKeyIn(fileKeys)).willReturn(fileKeys);

        // when
        pendingFileService.untrackPendingFiles(fileKeys);

        // then
        verify(pendingFileJpaRepository).deleteByFileKeyIn(fileKeys);
    }

    @DisplayName("추적된 fileKey만 삭제 활동 수행")
    @Test
    void testUntrackPendingFiles_givenNotTrackedFileKeys_willDeleteOnlyTrackedFileKeys() {
        // given
        String trackedFileKey = TEST_POST_CONTENT_FILE_KEY;
        String untrackedFileKey = TEST_MEMBER_PROFILE_FILE_KEY;
        List<String> fileKeys = List.of(trackedFileKey, untrackedFileKey);
        given(pendingFileJpaRepository.findFileKeysByFileKeyIn(fileKeys)).willReturn(List.of(trackedFileKey));

        // when
        pendingFileService.untrackPendingFiles(fileKeys);

        // then
        verify(pendingFileJpaRepository).deleteByFileKeyIn(List.of(trackedFileKey));
    }

    @DisplayName("null 목록으로 리포지토리 미호출")
    @Test
    void testUntrackPendingFiles_givenNull_willNotCallRepository() {
        // given & when
        pendingFileService.untrackPendingFiles(null);

        // then
        verify(pendingFileJpaRepository, never()).findFileKeysByFileKeyIn(any());
        verify(pendingFileJpaRepository, never()).deleteByFileKeyIn(any());
    }

    @DisplayName("빈 목록으로 리포지토리 미호출")
    @Test
    void testUntrackPendingFiles_givenEmptyFileKeyList_willNotCallRepository() {
        // given & when
        pendingFileService.untrackPendingFiles(List.of());

        // then
        verify(pendingFileJpaRepository, never()).findFileKeysByFileKeyIn(any());
        verify(pendingFileJpaRepository, never()).deleteByFileKeyIn(any());
    }

    @DisplayName("threshold 이전 만료 fileKey 목록 반환")
    @Test
    void testFindExpiredFileKeys_givenThreshold_willReturnList() {
        // given
        LocalDateTime threshold = LocalDateTime.now();
        given(pendingFileJpaRepository.findFileKeysByCreatedAtBefore(threshold)).willReturn(List.of(TEST_POST_CONTENT_FILE_KEY));

        // when
        List<String> result = pendingFileService.findExpiredFileKeys(threshold);

        // then
        assertThat(result).containsExactly(TEST_POST_CONTENT_FILE_KEY);
        verify(pendingFileJpaRepository).findFileKeysByCreatedAtBefore(threshold);
    }

    @DisplayName("threshold 이전 만료 레코드 삭제 활동 수행")
    @Test
    void testDeleteExpiredRecords_givenThreshold_willDeleteExpiredRecords() {
        // given
        LocalDateTime threshold = LocalDateTime.now();

        // when
        pendingFileService.deleteExpiredRecords(threshold);

        // then
        verify(pendingFileJpaRepository).deleteByCreatedAtBefore(threshold);
    }
}
