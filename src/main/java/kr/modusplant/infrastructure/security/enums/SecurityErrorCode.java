package kr.modusplant.infrastructure.security.enums;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    BAD_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "bad_password", "비밀번호가 틀렸습니다"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "bad_credentials", "인증에 필요한 요건이 없거나 부족합니다"),
    BANNED(HttpStatus.UNAUTHORIZED.value(), "banned", "밴 처리 된 계정입니다"),
    DELETED(HttpStatus.UNAUTHORIZED.value(), "deleted", "삭제된 계정입니다"),
    DISABLED_BY_LINKING(HttpStatus.UNAUTHORIZED.value(), "disabled_by_linking", "계정 연동으로 인해 비활성화된 계정입니다"),
    INACTIVE(HttpStatus.UNAUTHORIZED.value(), "inactive", "비활성화된 계정입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "access_denied", "접근이 거부되었습니다"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED.value(), "authentication_failed", "인증에 실패했습니다");

    private final int httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }

}
