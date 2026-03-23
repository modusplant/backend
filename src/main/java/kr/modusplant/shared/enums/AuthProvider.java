package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE("Google"),
    KAKAO("Kakao"),
    BASIC("Basic"),
    BASIC_GOOGLE("Basic & Google"),
    BASIC_KAKAO("Basic & Kakao");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }
}