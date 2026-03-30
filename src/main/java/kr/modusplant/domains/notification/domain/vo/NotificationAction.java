package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import kr.modusplant.shared.enums.NotificationActionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationAction {
    private final NotificationActionType action;

    public static NotificationAction create(NotificationActionType action) {
        if (action == null) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_NOTIFICATION_ACTION);
        }
        return new NotificationAction(action);
    }

    public boolean isPostRelatedAction() {
        return this.action == NotificationActionType.POST_LIKED;
    }

    public boolean isCommentRelatedAction() {
        return this.action == NotificationActionType.COMMENT_LIKED ||
                this.action == NotificationActionType.COMMENT_ADDED ||
                this.action == NotificationActionType.COMMENT_REPLY_ADDED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationAction notificationAction)) return false;
        return new EqualsBuilder().append(getAction(), notificationAction.getAction()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getAction()).toHashCode();
    }

}
