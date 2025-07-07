package kr.modusplant.global.error;

import kr.modusplant.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidInputException extends BusinessException {

    private final String inputName;

    public InvalidInputException(ErrorCode errorCode, String inputName) {
        super(errorCode);
        this.inputName = inputName;
    }

    // VerifyEmailRequest의 필드들
    public static InvalidInputException verifyCode() {
        return new InvalidInputException(ErrorCode.INVALID_EMAIL_VERIFY_CODE, "verifyCode");
    }

    public static InvalidInputException email() {
        return new InvalidInputException(ErrorCode.INVALID_EMAIL, "email");
    }
}
