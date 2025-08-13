package kr.modusplant.domain.exception.enums;

import kr.modusplant.domain.enums.HttpStatus;
import kr.modusplant.domain.exception.enums.supers.ResponseCode;
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
