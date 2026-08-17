package kr.modusplant.infrastructure.file.service;

import kr.modusplant.infrastructure.file.persistence.jpa.entity.PendingFileEntity;
import kr.modusplant.infrastructure.file.persistence.jpa.repository.PendingFileJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Presigned URL 기반 파일 업로드에서 고아파일(S3에는 있지만 DB에 파일키 등 메타데이터가 없는 파일)을 추적하고 정리하기 위한 전역 서비스.
 * <p>
 * 사용 도메인은 Presigned URL 발급 직후 {@link #trackPendingFiles}를,
 * 메타데이터 저장(또는 파일 교체 시 기존 fileKey) 성공 직후 {@link #untrackPendingFiles}를
 * 반드시 호출해야 고아파일 없이 정확하게 추적된다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PendingFileService {

    private final PendingFileJpaRepository pendingFileJpaRepository;

    /**
     * Presigned URL 발급 직후 호출하여 fileKey를 추적 대상으로 등록한다.
     * 호출자의 트랜잭션 상태(readOnly 등)와 무관하게 항상 즉시 커밋되도록
     * REQUIRES_NEW로 별도 트랜잭션을 연다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void trackPendingFiles(List<String> fileKeys) {
        if (fileKeys == null || fileKeys.isEmpty()) return;
        List<String> existingFileKeys = pendingFileJpaRepository.findFileKeysByFileKeyIn(fileKeys);
        List<String> newFileKeys = fileKeys.stream()
                .filter(fileKey -> !existingFileKeys.contains(fileKey))
                .toList();
        List<PendingFileEntity> entities = newFileKeys.stream()
                .map(fileKey -> PendingFileEntity.builder()
                        .fileKey(fileKey)
                        .domain(extractDomain(fileKey))
                        .build())
                .toList();
        pendingFileJpaRepository.saveAll(entities);
        log.debug("[PendingFile] Tracked {} file(s)", newFileKeys.size());
        if (fileKeys.size() != newFileKeys.size()) {
            log.warn("[PendingFile] Skipped {} already tracked file(s)", fileKeys.size() - newFileKeys.size());
        }
    }

    /**
     * 파일이 DB에 정상 반영된 직후 호출하여 추적 대상에서 해제한다.
     * 호출자의 쓰기 트랜잭션에 합류(REQUIRED)하므로, 호출자의 저장 로직이
     * 롤백되면 이 해제도 함께 롤백되어 파일이 계속 추적 상태로 남는다.
     * 반드시 실제 저장(save/update)이 성공한 이후에 호출해야 한다.
     */
    @Transactional
    public void untrackPendingFiles(List<String> fileKeys) {
        if (fileKeys == null || fileKeys.isEmpty()) return;
        List<String> existingFileKeys = pendingFileJpaRepository.findFileKeysByFileKeyIn(fileKeys);
        pendingFileJpaRepository.deleteByFileKeyIn(existingFileKeys);
        log.debug("[PendingFile] Untracked {} file(s)", existingFileKeys.size());
        if (fileKeys.size() != existingFileKeys.size()) {
            log.warn("[PendingFile] Skipped {} already untracked file(s)", fileKeys.size() - existingFileKeys.size());
        }
    }

    @Transactional(readOnly = true)
    public List<String> findExpiredFileKeys(LocalDateTime threshold) {
        return pendingFileJpaRepository.findFileKeysByCreatedAtBefore(threshold);
    }

    @Transactional
    public void deleteExpiredRecords(LocalDateTime threshold) {
        pendingFileJpaRepository.deleteByCreatedAtBefore(threshold);
    }

    /** fileKey의 최상위 경로를 도메인으로 사용한다. 예: "post/{ulid}/..." → "post" */
    private String extractDomain(String fileKey) {
        return fileKey.split("/")[0];
    }
}
