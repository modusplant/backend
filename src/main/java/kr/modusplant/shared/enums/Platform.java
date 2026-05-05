package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum Platform {
    WEB("Web"),
    ANDROID("Android"),
    IOS("iOS");

    private final String value;

    Platform(String value) {
        this.value = value;
    }

}
