package kr.modusplant.global.enums;

import kr.modusplant.global.enums.supers.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

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
