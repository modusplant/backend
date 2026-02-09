package kr.modusplant.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements kr.modusplant.shared.exception.supers.ErrorCode {

    // -- common errors --

    // -- business errors --
    // exists and not found

    // empty or invalid value
    NICKNAME_EMPTY(HttpStatus.BAD_REQUEST.value(), "nickname_empty", "닉네임이 비어 있습니다"),

    FORBIDDEN_EMAIL(HttpStatus.FORBIDDEN.value(), "forbidden_email", "해당 이메일에 접근할 수 없습니다"),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.FORBIDDEN.value(), "invalid_email_verify_code", "이메일의 검증 코드가 올바르지 않습니다"),

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "invalid_password", "비밀번호가 올바르지 않습니다"),
    INVALID_FILE_INPUT(HttpStatus.BAD_REQUEST.value(),"invalid_file_input","파일 입력이 올바르지 않습니다"),

    // others
    UNSUPPORTED_FILE(HttpStatus.FORBIDDEN.value(), "unsupported_file", "지원되지 않는 파일 타입입니다"),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.FORBIDDEN.value(), "unsupported_social_provider", "지원되지 않는 소셜 로그인 방식입니다"),

    FILE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST.value(),"file_limit_exceeded","파일 개수 또는 크기 제한을 초과했습니다"),


    // -- auth errors --
    CREDENTIAL_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "credential_not_authorized", "인증에 필요한 데이터가 없거나 유효하지 않습니다"),
    INTERNAL_AUTHENTICATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal_authentication_fail", "서버의 문제로 인증을 처리하지 못했습니다"),
    PASSWORD_RESET_EMAIL_VERIFY_FAIL(HttpStatus.BAD_REQUEST.value(), "password_reset_email_verify_fail","유효하지 않거나 만료된 링크입니다"),

    GOOGLE_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "google_login_fail", "구글 로그인 요청에 실패했습니다"),
    KAKAO_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "kakao_login_fail","카카오 로그인 요청에 실패했습니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
