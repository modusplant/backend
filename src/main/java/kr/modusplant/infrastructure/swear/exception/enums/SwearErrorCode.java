package kr.modusplant.infrastructure.swear.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SwearErrorCode implements ResponseCode {
    SWEAR_CONTAINED(HttpStatus.BAD_REQUEST, "swear_contained", "값에 비속어가 포함되어 있습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
