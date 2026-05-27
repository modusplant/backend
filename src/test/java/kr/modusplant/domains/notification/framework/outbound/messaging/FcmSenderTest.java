package kr.modusplant.domains.notification.framework.outbound.messaging;

import com.google.firebase.messaging.*;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmSenderTest implements NotificationTestUtils {
    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private FcmSender fcmSender;

    @Test
    @DisplayName("토큰이 없으면 전송하지 않기")
    void testSendAsync_givenNotificationWithoutToken_willNotSend() {
        // given
        given(fcmTokenRepository.findTokensByRecipientId(any())).willReturn(List.of());

        // when
        fcmSender.sendAsync(createPostLikedUnreadNotification(LocalDateTime.now()));

        // then
        verifyNoInteractions(firebaseMessaging);
    }

    @Test
    @DisplayName("만료된 토큰은 삭제")
    void testSendAsync_givenNotificationWithExpiredToken_willDeleteToken() throws FirebaseMessagingException {
        // given
        String expiredToken = "expired_token";
        given(fcmTokenRepository.findTokensByRecipientId(any())).willReturn(List.of(expiredToken));

        SendResponse failResponse = mock(SendResponse.class);
        FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
        given(failResponse.isSuccessful()).willReturn(false);
        given(failResponse.getException()).willReturn(exception);
        given(exception.getMessagingErrorCode()).willReturn(MessagingErrorCode.UNREGISTERED);

        BatchResponse batchResponse = mock(BatchResponse.class);
        given(batchResponse.getResponses()).willReturn(List.of(failResponse));
        given(firebaseMessaging.sendEachForMulticast(any())).willReturn(batchResponse);

        // when
        fcmSender.sendAsync(createPostLikedUnreadNotification(LocalDateTime.now()));

        // then
        verify(fcmTokenRepository).deleteByToken(expiredToken);
    }

}