package kr.modusplant.infrastructure.exception.enums;

import kr.modusplant.infrastructure.exception.enums.supers.ResponseCode;
import kr.modusplant.infrastructure.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResponseCode {

    GENERIC_SUCCESS(HttpStatus.OK, "generic_success", "");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return true;
    }
}
