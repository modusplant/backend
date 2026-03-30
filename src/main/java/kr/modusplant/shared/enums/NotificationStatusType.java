package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum NotificationStatusType {
    UNREAD("unread"),
    READ("read");

    private String value;

    NotificationStatusType(String value) {
        this.value = value;
    }
}
