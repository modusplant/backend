package kr.modusplant.shared.exception.model;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicErrorCode implements ErrorCode{

    private final int httpStatus;
    private final String code;
    private final String message;

    public static DynamicErrorCode create(ErrorCode errorCode, String message) {
        return new DynamicErrorCode(errorCode.getHttpStatus(), errorCode.getCode(), message);
    }
}
