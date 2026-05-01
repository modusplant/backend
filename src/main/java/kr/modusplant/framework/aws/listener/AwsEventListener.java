package kr.modusplant.framework.aws.listener;

import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.shared.event.ImageRemoveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AwsEventListener {
    private final S3FileService s3FileService;

    @Async("awsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageRemove(ImageRemoveEvent event) {
        try {
            s3FileService.deleteFiles(event.getImageFileKeys());
        } catch (Exception e) {
            log.error("[AWS] S3 파일 이미지 제거 실패 - imageFileKeys = {}", event.getImageFileKeys(), e);
        }
    }
}
