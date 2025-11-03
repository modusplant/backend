package kr.modusplant.domains.normalidentity.normal.usecase.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    SIGNUP_VERIFY_EMAIL("signupVerifyEmail"),
    RESET_PASSWORD_EMAIL("resetPasswordEmail");

    private final String value;

    EmailType(String value) {
        this.value = value;
    }
}
