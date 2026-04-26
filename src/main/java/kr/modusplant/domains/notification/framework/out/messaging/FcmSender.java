package kr.modusplant.domains.notification.framework.out.messaging;

import com.google.firebase.messaging.*;
import kr.modusplant.domains.notification.domain.aggregate.Notification;
import kr.modusplant.domains.notification.domain.vo.NotificationAction;
import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmSender {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    @Async("notificationExecutor")
    public void sendAsync(Notification notification) {
        List<String> tokens = fcmTokenRepository.findTokensByRecipientId(notification.getRecipientId());
        if (tokens.isEmpty()) return;

        // 멀티캐스트 : 한 유저의 여러 기기에 동시 전송
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(
                        com.google.firebase.messaging.Notification.builder()
                                .setTitle(buildTitle(notification))
                                .setBody(buildBody(notification))
                                .build()
                )
                .putData("notificationId", notification.getNotificationId().getValue())
                .putData("type", resolveContentType(notification.getAction()))
                .putData("postId", notification.getPostId().getValue())
                .putData("commentPath", notification.getCommentPath() != null ? notification.getCommentPath().getPath() : "")
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            handleResponse(tokens, response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 전송 실패 recipientId={}", notification.getRecipientId().getValue(), e);
        }
    }

    private String buildTitle(Notification notification) {
        return switch (notification.getAction().getAction()) {
            case POST_LIKED -> "게시글 좋아요";
            case COMMENT_LIKED -> "댓글 좋아요";
            case COMMENT_ADDED -> "새 댓글";
            case COMMENT_REPLY_ADDED -> "새 대댓글";
        };
    }

    private String buildBody(Notification notification) {
        String nickname = notification.getActor().getNickname();
        return switch (notification.getAction().getAction()) {
            case POST_LIKED -> "내 게시글이 좋아요를 받았어요";
            case COMMENT_LIKED -> "내 댓글이 좋아요를 받았어요.";
            case COMMENT_ADDED, COMMENT_REPLY_ADDED -> nickname + "님이 댓글을 달았어요.";
        };
    }

    private String resolveContentType(NotificationAction action) {
        return switch (action.getAction()) {
            case POST_LIKED -> "post";
            case COMMENT_LIKED, COMMENT_ADDED, COMMENT_REPLY_ADDED -> "comment";
        };
    }

    private void handleResponse(List<String> tokens, BatchResponse response) {
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                MessagingErrorCode code = responses.get(i).getException().getMessagingErrorCode();
                // 만료되거나 등록 해제된 토큰은 즉시 삭제
                if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                    fcmTokenRepository.deleteByToken(tokens.get(i));
                }
            }
        }
    }
}
