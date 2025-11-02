package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE("Google"),
    KAKAO("Kakao"),
    BASIC("Basic");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }
}