package kr.modusplant.global.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {
    OK("OK"),
    BAD_REQUEST("BAD_REQUEST"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    NOT_FOUND("NOT_FOUND"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

    private final String value;

    ResponseCode(String value) {
        this.value = value;
    }
}