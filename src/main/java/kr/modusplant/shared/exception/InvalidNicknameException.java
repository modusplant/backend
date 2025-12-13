package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class InvalidNicknameException extends BusinessException {
    public InvalidNicknameException() {
        super(ErrorCode.INVALID_NICKNAME);
    }
}
