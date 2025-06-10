package kr.modusplant.global.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    RESPONSE_MESSAGE_200("OK: Succeeded"),
    RESPONSE_MESSAGE_400("Bad Request: Failed due to client error"),
    RESPONSE_MESSAGE_401("Unauthorized: Invalid or missing authentication credentials"),
    RESPONSE_MESSAGE_500("Internal Server Error: Failed due to server error");

    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }
}
