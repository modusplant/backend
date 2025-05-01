package kr.modusplant.global.enums;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    EXISTED_ENTITY("Existed entity with the name - value: "),
    NOT_FOUND_ENTITY("Not found entity with the name - value: "),
    FOR_THE_CLASS(" for the class ");

    private final String value;

    ExceptionMessage(String value) {
        this.value = value;
    }
}
