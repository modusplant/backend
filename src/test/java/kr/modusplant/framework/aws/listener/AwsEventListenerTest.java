package kr.modusplant.framework.aws.listener;

import kr.modusplant.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.shared.event.common.util.ImageRemoveEventTestUtils.testImageRemoveEvent;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AwsEventListenerTest {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final AwsEventListener awsEventListener = new AwsEventListener(amazonS3Service);

    @Test
    @DisplayName("이미지 제거 이벤트 수신 시 S3에서 파일을 제거한다")
    void handleImageRemove_shouldRemoveImageFromS3() {
        // given
        willDoNothing().given(amazonS3Service).deleteFiles(anyList());

        // when
        awsEventListener.handleImageRemove(testImageRemoveEvent);

        // then
        verify(amazonS3Service, times(1)).deleteFiles(testImageRemoveEvent.getImageFileKeys());
    }
}