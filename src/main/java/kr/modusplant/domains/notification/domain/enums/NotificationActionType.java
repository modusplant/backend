package kr.modusplant.domains.notification.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationActionType {
    POST_LIKED,
    COMMENT_LIKED,
    COMMENT_ADDED,
    COMMENT_REPLY_ADDED;
}
