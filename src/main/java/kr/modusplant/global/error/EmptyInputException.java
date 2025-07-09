package kr.modusplant.global.error;

import kr.modusplant.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class EmptyInputException extends BusinessException {

    private final String inputName;

    private EmptyInputException(ErrorCode errorCode, String inputName) {
        super(errorCode);
        this.inputName = inputName;
    }

    public static EmptyInputException postUlid() {
        return new EmptyInputException(ErrorCode.EMPTY_POST_IDENTIFIER, "postUlid"); }

    public static EmptyInputException path() {
        return new EmptyInputException(ErrorCode.EMPTY_PATH, "path");
    }
}
