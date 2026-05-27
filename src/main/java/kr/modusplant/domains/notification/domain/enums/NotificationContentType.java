package kr.modusplant.domains.notification.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationContentType {
    POST("post"),
    COMMENT("comment");

    private final String value;

    NotificationContentType(String value) {
        this.value = value;
    }
}
