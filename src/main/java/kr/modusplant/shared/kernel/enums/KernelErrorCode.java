package kr.modusplant.shared.kernel.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KernelErrorCode implements ResponseCode {

    // Empty
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "empty_email","이메일이 비었습니다"),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "nickname_empty", "닉네임이 비어 있습니다"),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "password_empty", "비밀번호가 비어 있습니다"),

    // Not Found
    NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST, "empty_email","이메일이 비었습니다"),
    NOT_FOUND_NICKNAME(HttpStatus.BAD_REQUEST, "nickname_empty", "닉네임이 비어 있습니다"),
    NOT_FOUND_PASSWORD(HttpStatus.BAD_REQUEST, "password_empty", "비밀번호가 비어 있습니다"),

    // Exists
    EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "empty_email","이메일이 비었습니다"),
    EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "nickname_empty", "닉네임이 비어 있습니다"),
    EXISTS_PASSWORD(HttpStatus.BAD_REQUEST, "password_empty", "비밀번호가 비어 있습니다"),

    // Invalid
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "invalid_email", "이메일 형식이 올바르지 않습니다"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "invalid_nickname", "닉네임 형식이 올바르지 않습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "invalid_password", "비밀번호 형식이 올바르지 않습니다"),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
