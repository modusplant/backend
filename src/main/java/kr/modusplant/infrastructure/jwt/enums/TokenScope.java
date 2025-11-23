package kr.modusplant.infrastructure.jwt.enums;

import lombok.Getter;

@Getter
public enum TokenScope {
    AUTH_CODE_EMAIL("authCodeEmail"),
    RESET_PASSWORD_EMAIL("resetPasswordEmail"),
    RESET_PASSWORD_INPUT("resetPasswordInput");

    private final String value;

    TokenScope(String value) {
        this.value = value;
    }
}
