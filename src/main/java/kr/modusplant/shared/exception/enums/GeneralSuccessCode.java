package kr.modusplant.shared.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralSuccessCode implements ResponseCode {

    GENERIC_SUCCESS(HttpStatus.OK.value(), "generic_success", "");

    private final int httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return true;
    }
}
