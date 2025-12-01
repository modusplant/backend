package kr.modusplant.domains.identity.email.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailIdentityErrorCode implements ResponseCode {

    NOT_SENDABLE_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "not_sendable_email", "서버에서 이메일을 보낼 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
