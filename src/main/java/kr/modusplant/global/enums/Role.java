package kr.modusplant.global.enums;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER("User"),
    ROLE_ADMIN("Admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}
