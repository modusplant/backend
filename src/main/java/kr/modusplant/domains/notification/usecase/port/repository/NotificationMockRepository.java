package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.notification.domain.vo.*;

// TODO: 알림 생성 로직 완성 후 삭제
public interface NotificationMockRepository {
    void saveMockNotification(RecipientId recipientId, NotificationAction action, PostId postId, CommentPath commentPath, ContentPreview contentPreview);

    void deleteMockNotification(RecipientId recipientId);
}
