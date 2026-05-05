package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum ContentType {
    POST("post"),
    COMMENT("comment");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }
}
