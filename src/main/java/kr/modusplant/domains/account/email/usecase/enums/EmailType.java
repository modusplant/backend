package kr.modusplant.domains.account.email.usecase.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    AUTHENTICATION_CODE_EMAIL("authenticationCodeEmail"),
    RESET_PASSWORD_EMAIL("resetPasswordEmail");

    private final String value;

    EmailType(String value) {
        this.value = value;
    }
}
