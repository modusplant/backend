package kr.modusplant.global.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    RESPONSE_MESSAGE_200("성공했습니다."),
    RESPONSE_MESSAGE_400("잘못된 요청입니다."),
    RESPONSE_MESSAGE_401("인증되지 않은 유저입니다."),
    RESPONSE_MESSAGE_500("서버에 문제가 발생했습니다.");

    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }
}
