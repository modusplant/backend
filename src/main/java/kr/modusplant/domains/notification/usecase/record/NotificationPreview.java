package kr.modusplant.domains.notification.usecase.record;

import java.util.UUID;

public record NotificationPreview(
        UUID authorUuid,
        String contentPreview
) {
}
