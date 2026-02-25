package kr.modusplant.infrastructure.jwt.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthTokenErrorCode implements ErrorCode {

    CREDENTIAL_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "credential_not_authorized", "인증에 필요한 데이터가 없거나 유효하지 않습니다"),
    INTERNAL_AUTHENTICATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal_authentication_fail", "서버의 문제로 인증을 처리하지 못했습니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
