package kr.modusplant.domains.identity.domain.vo.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public static boolean isValidStatus(String input) {
        for (UserRole type : UserRole.values()) {
            if(type.getValue().equals(input)) {
                return true;
            }
        }
        return false;
    }
}
