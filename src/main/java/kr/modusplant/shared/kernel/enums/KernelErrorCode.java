package kr.modusplant.shared.kernel.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum KernelErrorCode implements ResponseCode {

    // Empty
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST.value(), "empty_email","이메일 값이 비었습니다"),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST.value(), "nickname_empty", "닉네임 값이 비었습니다"),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST.value(), "password_empty", "비밀번호 값이 비었습니다"),

    // Not Found
    NOT_FOUND_EMAIL(HttpStatus.BAD_REQUEST.value(), "not_found_email","존재하지 않는 이메일입니다"),
    NOT_FOUND_NICKNAME(HttpStatus.BAD_REQUEST.value(), "nickname_empty", "존재하지 않는 닉네임입니다"),
    NOT_FOUND_PASSWORD(HttpStatus.BAD_REQUEST.value(), "password_empty", "존재하지 않는 비밀번호입니다"),

    // Exists
    EXISTS_EMAIL(HttpStatus.BAD_REQUEST.value(), "empty_email","이메일이 이미 존재합니다"),
    EXISTS_NICKNAME(HttpStatus.BAD_REQUEST.value(), "nickname_empty", "닉네임이 이미 존재합니다"),
    EXISTS_PASSWORD(HttpStatus.BAD_REQUEST.value(), "password_empty", "비밀번호가 이미 존재합니다"),

    // Invalid
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST.value(), "invalid_email_format", "이메일의 형식이 유효하지 않습니다"),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST.value(), "invalid_nickname_format", "닉네임의 형식이 유효하지 않습니다"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST.value(), "invalid_password_format", "비밀번호의 형식이 유효하지 않습니다"),

    ;

    private final int httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
