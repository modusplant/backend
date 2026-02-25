package kr.modusplant.domains.account.email.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailIdentityErrorCode implements ErrorCode {

    NOT_SENDABLE_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "not_sendable_email", "서버에서 이메일을 보낼 수 없습니다"),
    MEMBER_NOT_FOUND_WITH_EMAIL(HttpStatus.BAD_REQUEST.value(), "member_not_found_with_email", "해당 이메일을 가진 사용자가 존재하지 않습니다"),
    FORBIDDEN_EMAIL(HttpStatus.FORBIDDEN.value(), "forbidden_email", "해당 이메일에 접근할 수 없습니다"),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.FORBIDDEN.value(), "invalid_email_verify_code", "이메일의 검증 코드가 올바르지 않습니다"),
    INVALID_EMAIL_VERIFY_LINK(HttpStatus.BAD_REQUEST.value(), "invalid_email_verify_link","유효하지 않거나 만료된 링크입니다")
    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
