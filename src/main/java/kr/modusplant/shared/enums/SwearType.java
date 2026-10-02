package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum SwearType {
    GENERAL("general"),
    FAMILY("family"),
    SEXUAL("sexual");

    private final String value;

    SwearType(String value) {
        this.value = value;
    }
}
