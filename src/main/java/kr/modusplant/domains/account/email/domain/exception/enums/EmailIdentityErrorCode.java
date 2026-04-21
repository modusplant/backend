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
    INVALID_EMAIL_VERIFY_LINK(HttpStatus.BAD_REQUEST.value(), "invalid_email_verify_link","유효하지 않거나 만료된 링크입니다"),
    ALREADY_REGISTERED_GOOGLE_EMAIL(HttpStatus.CONFLICT.value(), "already_registered_google_email", "이미 구글 소셜 회원으로 가입된 이메일입니다"),
    ALREADY_REGISTERED_KAKAO_EMAIL(HttpStatus.CONFLICT.value(), "already_registered_kako_email", "이미 카카오 소셜 회원으로 가입된 이메일입니다"),
    ALREADY_REGISTERED_BASIC_EMAIL(HttpStatus.CONFLICT.value(), "already_registered_basic_email", "이미 일반회원으로 가입된 이메일입니다"),
    ALREADY_REGISTERED_BASIC_GOOGLE_EMAIL(HttpStatus.CONFLICT.value(), "already_registered_basic_google_email", "이미 일반회원(구글 연동)으로 가입된 이메일입니다"),
    ALREADY_REGISTERED_BASIC_KAKAO_EMAIL(HttpStatus.CONFLICT.value(), "already_registered_basic_kakao_email", "이미 일반회원(카카오 연동)으로 가입된 이메일입니다")
    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
