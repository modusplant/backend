package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum NotificationActionType {
    POST_LIKED("POST_LIKED"),
    COMMENT_LIKED("COMMENT_LIKED"),
    COMMENT_ADDED("COMMENT_ADDED"),
    COMMENT_REPLY_ADDED("COMMENT_REPLY_ADDED");

    private final String value;

    NotificationActionType(String value) {
        this.value = value;
    }

}
