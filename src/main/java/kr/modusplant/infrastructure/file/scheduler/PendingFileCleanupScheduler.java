package kr.modusplant.infrastructure.file.scheduler;

import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingFileCleanupScheduler {

    private final PendingFileService pendingFileService;
    private final AmazonS3Service amazonS3Service;

    @Scheduled(cron = "${scheduler.pending-file-cleanup-cron}", zone = "Asia/Seoul")
    public void cleanup() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<String> fileKeys = pendingFileService.findExpiredFileKeys(threshold);
        if (fileKeys.isEmpty()) return;

        try {
            amazonS3Service.deleteFiles(fileKeys);
            log.info("[PendingFile] S3 고아 파일 {}개 삭제 완료", fileKeys.size());
        } catch (Exception e) {
            log.error("[PendingFile] S3 고아 파일 삭제 실패 — 다음 스케줄 사이클에 재시도 예정 (size={})", fileKeys.size(), e);
            return;
        }

        pendingFileService.deleteExpiredRecords(threshold);
        log.info("[PendingFile] DB pending_file 레코드 {}개 정리 완료", fileKeys.size());
    }
}
