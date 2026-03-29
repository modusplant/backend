package kr.modusplant.domains.notification.usecase.record;

import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationReadModel(
        String ulid,
        UUID recipientId,
        UUID actorId,
        String actorNickname,
        NotificationActionType action,
        NotificationStatusType status,
        String postUlid,
        String commentPath,
        String contentPreview,
        LocalDateTime createdAt
) {
}
