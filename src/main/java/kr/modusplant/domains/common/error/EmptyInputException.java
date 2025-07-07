package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class EmptyInputException extends BusinessException {

    private final String inputName;

    public EmptyInputException(ErrorCode errorCode, String inputName) {
        super(errorCode);
        this.inputName = inputName;
    }

    public static EmptyInputException postUlid() {
        return new EmptyInputException(ErrorCode.EMPTY_POSTULID_INPUT, "postUlid"); }

    public static EmptyInputException path() {
        return new EmptyInputException(ErrorCode.EMPTY_PATH_INPUT, "path");
    }
}
