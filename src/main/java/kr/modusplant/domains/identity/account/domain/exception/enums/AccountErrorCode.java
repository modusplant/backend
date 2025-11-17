package kr.modusplant.domains.identity.account.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ResponseCode {

    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "invalid_email", "올바른 이메일 형식이 아닙니다"),

    EMPTY_MEMBER_ID(HttpStatus.BAD_REQUEST, "empty_member_id", "사용자의 식별자가 비어 있습니다"),
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "empty_email", "이메일이 비어 있습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
