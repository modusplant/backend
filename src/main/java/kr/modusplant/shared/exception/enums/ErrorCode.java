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

    // others


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
