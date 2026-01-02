package kr.modusplant.shared.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeneralErrorCode implements ResponseCode {

    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", "서버에 문제가 발생했습니다"),
    MISMATCH_INPUT_TYPE(HttpStatus.BAD_REQUEST, "mismatch_input_type", "입력값의 타입이 올바르지 않습니다"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "invalid_input", "입력값이 유효하지 않습니다"),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "constraint_violation", "데이터의 조건이 위배되었습니다"),
    MALFORMED_INPUT(HttpStatus.BAD_REQUEST, "malformed_input", "입력값의 형식이 올바르지 않습니다"),
    UNEXPECTED_INPUT(HttpStatus.BAD_REQUEST, "unexpected_input", "서버가 알 수 없는 입력값이 발견되었습니다"),
    INVALID_STATE(HttpStatus.CONFLICT, "invalid_state", "리소스의 상태가 유효하지 않습니다"),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
