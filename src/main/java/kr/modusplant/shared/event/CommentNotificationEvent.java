package kr.modusplant.shared.event;

import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentNotificationEvent {
    private final UUID actorId;
    private final String postUlid;
    private final String commentPath;
    private final String contentPreview;
    private final NotificationActionType action;

    public static CommentNotificationEvent create(UUID actorId, String postUlid, String commentPath, String contentPreview) {
        validate(actorId, postUlid, commentPath);
        return new CommentNotificationEvent(actorId, postUlid, commentPath, contentPreview, getActionType(commentPath));
    }

    private static void validate(UUID actorId, String postUlid, String commentPath) {
        if (actorId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_ACTOR, "actorId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST,"postUlid");
        }
        if (commentPath == null || commentPath.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "commentPath");
        }
    }

    private static NotificationActionType getActionType(String commentPath) {
        return commentPath.contains(".")
                ? NotificationActionType.COMMENT_REPLY_ADDED
                : NotificationActionType.COMMENT_ADDED;
    }
}
