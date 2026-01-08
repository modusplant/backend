package kr.modusplant.shared.exception.enums;

import kr.modusplant.shared.exception.enums.supers.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralSuccessCode implements SuccessCode {

    GENERIC_SUCCESS(HttpStatus.OK.value(), "generic_success", "");

    private final int httpStatus;
    private final String code;
    private final String message;
}
