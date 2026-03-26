package kr.modusplant.domains.notification.domain.aggregate;

import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import kr.modusplant.domains.notification.domain.vo.*;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {
    private final NotificationId notificationId;
    private RecipientId recipientId;
    private ActorId actorId;
    private NotificationAction action;
    private NotificationStatus status;
    private PostId postId;
    private CommentPath commentPath;
    private ContentPreview contentPreview;
    private LocalDateTime createdAt;

    public static Notification create(NotificationId notificationId, RecipientId recipientId, ActorId actorId, NotificationAction action, NotificationStatus status, PostId postId, CommentPath commentPath, ContentPreview contentPreview) {
        if (notificationId == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_NOTIFICATION_ID);
        } else if (recipientId == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_RECIPIENT_ID);
        } else if (actorId == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_ACTOR_ID);
        } else if (action == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_NOTIFICATION_ACTION);
        } else if (status == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_NOTIFICATION_STATUS);
        } else if (postId == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_POST_ID);
        } else if (action.isCommentRelatedAction() && commentPath == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_COMMENT_PATH);
        }
        return new Notification(notificationId, recipientId, actorId, action, status, postId, commentPath, contentPreview, LocalDateTime.now());
    }

    public void read() {
        this.status = NotificationStatus.read();
    }

    public boolean isExpired() {
        return this.createdAt.isBefore(LocalDateTime.now().minusDays(30));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if(!(o instanceof Notification notification)) return false;

        return new EqualsBuilder().append(getNotificationId(),notification.getNotificationId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getNotificationId()).toHashCode();
    }

}
