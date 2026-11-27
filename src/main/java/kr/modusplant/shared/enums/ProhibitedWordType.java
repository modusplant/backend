package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum ProhibitedWordType {
    GENERAL("general"),
    FAMILY("family"),
    SEXUAL("sexual");

    private final String value;

    ProhibitedWordType(String value) {
        this.value = value;
    }
}
