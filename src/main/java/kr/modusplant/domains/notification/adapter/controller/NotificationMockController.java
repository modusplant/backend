package kr.modusplant.domains.notification.adapter.controller;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.notification.usecase.port.repository.NotificationMockRepository;
import kr.modusplant.shared.enums.NotificationActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

// TODO: 알림 생성 로직 완성 후 삭제
@Component
@RequiredArgsConstructor
@Profile({"local", "dev"})
@Transactional
public class NotificationMockController {
    private final NotificationMockRepository repository;

    public void createMockNotification(UUID memberUuid, NotificationActionType action, String postUlid, String commentPath, String contentPreview) {
        repository.saveMockNotification(
                RecipientId.fromUuid(memberUuid),
                NotificationAction.create(action),
                PostId.create(postUlid),
                action == NotificationActionType.POST_LIKED ? null : CommentPath.create(commentPath),
                ContentPreview.create(contentPreview)
        );
    }

    public void removeMockNotification(UUID memberUuid) {
        repository.deleteMockNotification(RecipientId.fromUuid(memberUuid));
    }

}
