package kr.modusplant.global.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    RESPONSE_MESSAGE_200("OK: Succeeded");

    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }
}
