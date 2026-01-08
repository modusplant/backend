package kr.modusplant.infrastructure.swear.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SwearErrorCode implements ErrorCode {
    SWEAR_CONTAINED(HttpStatus.BAD_REQUEST.value(), "swear_contained", "값에 비속어가 포함되어 있습니다. ");

    private final int httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
