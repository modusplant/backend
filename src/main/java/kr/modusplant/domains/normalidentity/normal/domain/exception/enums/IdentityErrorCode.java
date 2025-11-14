package kr.modusplant.domains.normalidentity.normal.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdentityErrorCode implements ResponseCode {

    // domain
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "invalid_role", "올바른 사용자의 역할이 아닙니다"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "invalid_email", "올바른 이메일 형식이 아닙니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "invalid_password", "올바른 비밀번호 형식이 아닙니다"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "invalid_nickname", "올바른 닉네임 형식이 아닙니다"),
    INVALID_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST, "invalid_agreed_terms_of_version", "동의한 약관의 버전 값이 올바른 형식이 아닙니다"),

    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "empty_email", "이메일이 비어 있습니다"),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "empty_password", "비밀번호가 비어 있습니다"),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "empty_nickname", "닉네임이 비어 있습니다"),
    EMPTY_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST, "empty_agreed_terms_of_version", "동의한 약관의 버전 값이 비어 있습니다"),

    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "nickname_already_exists", "해당 닉네임이 이미 존재합니다"),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "member_already_exists", "해당 사용자가 이미 존재합니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
