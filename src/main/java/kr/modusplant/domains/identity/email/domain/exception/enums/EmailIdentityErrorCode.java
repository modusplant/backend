package kr.modusplant.domains.identity.email.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailIdentityErrorCode implements ResponseCode {

    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "invalid_email", "유효한 이메일의 형식이 아닙니다"),
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "empty_email", "이메일의 값이 비었습니다"),

    NOT_SENDABLE_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "not_sendable_email", "서버에서 이메일을 보낼 수 없습니다"),
    MEMBER_NOT_FOUND_WITH_EMAIL(HttpStatus.BAD_REQUEST, "member_not_found_with_email", "해당 이메일을 가진 사용자가 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
