package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class EmptyNicknameException extends BusinessException {
    public EmptyNicknameException() {
        super(ErrorCode.NICKNAME_EMPTY);
    }
}
