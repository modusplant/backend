package kr.modusplant.domains.account.normal.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NormalIdentityErrorCode implements ErrorCode {

    // domain
    INVALID_CODE(HttpStatus.BAD_REQUEST.value(), "invalid_code", "올바른 코드가 아닙니다"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST.value(), "invalid_role", "올바른 사용자의 역할이 아닙니다"),
    INVALID_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST.value(), "invalid_agreed_terms_of_version", "동의한 약관의 버전 값이 올바른 형식이 아닙니다"),
    EMPTY_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST.value(), "empty_agreed_terms_of_version", "동의한 약관의 버전 값이 비어 있습니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
