package kr.modusplant.shared.framework.aws.listener;

import kr.modusplant.shared.framework.aws.event.ImageRemoveTask;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
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
    private final AmazonS3Service amazonS3Service;

    @Async("awsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageRemove(ImageRemoveTask event) {
        try {
            amazonS3Service.deleteFiles(event.getImageFileKey());
        } catch (Exception e) {
            log.error("[AWS] S3 파일 이미지 제거 실패 - imageFileKey = {}", event.getImageFileKey(), e);
        }
    }

    @Async("awsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageRemove(ImagesRemoveTask event) {
        try {
            amazonS3Service.deleteFiles(event.getImageFileKeys());
        } catch (Exception e) {
            log.error("[AWS] S3 파일 이미지 제거 실패 - imageFileKeys = {}", event.getImageFileKeys(), e);
        }
    }
}
