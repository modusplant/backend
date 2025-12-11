package kr.modusplant.infrastructure.swear.enums;

import lombok.Getter;

@Getter
public enum SwearType {
    SEXUAL("sexual"),
    FAMILY("family"),
    GENERAL("general");

    private final String value;

    SwearType(String value) {
        this.value = value;
    }
}
