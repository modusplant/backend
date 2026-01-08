package kr.modusplant.infrastructure.security.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ResponseCode {

    BAD_PASSWORD(HttpStatus.UNAUTHORIZED, "bad_password", "비밀번호가 틀렸습니다"),
    BAD_PASSWORD_FORMAT(HttpStatus.UNAUTHORIZED, "bad_password_format", "비밀번호의 값이 형식에 어긋납니다"),
    BAD_EMAIL_FORMAT(HttpStatus.UNAUTHORIZED, "bad_email_format", "이메일의 값이 형식에 어긋납니다"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "bad_credentials", "인증에 필요한 요건이 없거나 부족합니다"),

    MEMBER_STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "member_state_not_found", "사용자의 계정 정보를 찾을 수 없습니다"),
    MEMBER_AUTH_STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "member_auth_state_not_found", "사용자의 인증 정보를 찾을 수 없습니다"),
    MEMBER_ROLE_STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "member_role_state_not_found", "사용자의 역할 정보를 찾을 수 없습니다"),

    BANNED(HttpStatus.UNAUTHORIZED, "banned", "밴 처리 된 계정입니다"),
    DELETED(HttpStatus.UNAUTHORIZED, "deleted", "삭제된 계정입니다"),
    DISABLED_BY_LINKING(HttpStatus.UNAUTHORIZED, "disabled_by_linking", "계정 연동으로 인해 비활성화된 계정입니다"),
    INACTIVE(HttpStatus.UNAUTHORIZED, "inactive", "비활성화된 계정입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "access_denied", "접근이 거부되었습니다"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "authentication_failed", "인증에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }

}
