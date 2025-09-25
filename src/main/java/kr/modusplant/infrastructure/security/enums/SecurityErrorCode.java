package kr.modusplant.infrastructure.security.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ResponseCode {

    BAD_PASSWORD(HttpStatus.UNAUTHORIZED, "bad_password", "비밀번호가 틀렸습니다"),
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
