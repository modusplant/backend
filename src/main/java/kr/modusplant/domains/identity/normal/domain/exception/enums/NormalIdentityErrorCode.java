package kr.modusplant.domains.identity.normal.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NormalIdentityErrorCode implements ResponseCode {

    // domain
    INVALID_CODE(HttpStatus.BAD_REQUEST, "invalid_code", "올바른 코드가 아닙니다"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "invalid_role", "올바른 사용자의 역할이 아닙니다"),
    INVALID_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST, "invalid_agreed_terms_of_version", "동의한 약관의 버전 값이 올바른 형식이 아닙니다"),
    EMPTY_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST, "empty_agreed_terms_of_version", "동의한 약관의 버전 값이 비어 있습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
