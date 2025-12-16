package kr.modusplant.shared.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResponseCode {

    // -- common errors --
    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", "서버에 문제가 발생했습니다"),

    INPUT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "input_type_mismatch", "입력값의 서식이 올바르지 않습니다"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "invalid_input", "입력값이 유효하지 않습니다"),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "constraint_violation", "데이터에 설정된 조건을 위배했습니다"),
    MALFORMED_INPUT(HttpStatus.BAD_REQUEST, "malformed_input", "입력값의 형식이 올바르지 않습니다"),
    UNEXPECTED_INPUT(HttpStatus.BAD_REQUEST, "unexpected_input", "서버가 알 수 없는 입력값이 발견되었습니다"),

    INVALID_STATE(HttpStatus.CONFLICT, "invalid_state", "리소스의 상태가 유효하지 않습니다"),

    // -- business errors --
    // exists and not found
    NICKNAME_EXISTS(HttpStatus.CONFLICT, "nickname_exists", "닉네임이 이미 존재합니다"),
    MEMBER_EXISTS(HttpStatus.CONFLICT, "member_exists", "사용자의 계정이 이미 존재합니다"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    MEMBER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "comment_not_found", "댓글을 찾을 수 없습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),

    // empty or invalid value
    CONTENT_TYPE_EMPTY(HttpStatus.BAD_REQUEST, "content_type_empty", "컨텐츠 타입이 비었습니다"),
    EMAIL_EMPTY(HttpStatus.BAD_REQUEST, "empty_email","이메일이 비었습니다"),
    FILE_NAME_EMPTY(HttpStatus.BAD_REQUEST, "file_name_empty", "파일명이 비었습니다"),
    NICKNAME_EMPTY(HttpStatus.BAD_REQUEST, "nickname_empty", "닉네임이 비어 있습니다"),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "password_empty", "비밀번호가 비어 있습니다"),

    FORBIDDEN_EMAIL(HttpStatus.FORBIDDEN, "forbidden_email", "해당 이메일에 접근할 수 없습니다"),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.FORBIDDEN, "invalid_email_verify_code", "이메일의 검증 코드가 올바르지 않습니다"),

    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "invalid_email", "이메일이 올바르지 않습니다"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "invalid_nickname", "닉네임이 올바르지 않습니다"),
    INVALID_PAGE_RANGE(HttpStatus.BAD_REQUEST, "invalid_page_range", "이용할 수 있는 페이지 범위가 아닙니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "invalid_password", "비밀번호가 올바르지 않습니다"),
    INVALID_FILE_INPUT(HttpStatus.BAD_REQUEST,"invalid_file_input","파일 입력이 올바르지 않습니다"),

    // others
    UNSUPPORTED_FILE(HttpStatus.FORBIDDEN, "unsupported_file", "지원되지 않는 파일 타입입니다"),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.FORBIDDEN, "unsupported_social_provider", "지원되지 않는 소셜 로그인 방식입니다"),

    FILE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,"file_limit_exceeded","파일 개수 또는 크기 제한을 초과했습니다"),

    SPECIFIED_SORTING_METHOD(HttpStatus.BAD_REQUEST, "specified_sorting_method", "페이지 정렬 방식은 지정되지 않아야 합니다"),

    // -- auth errors --
    CREDENTIAL_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "credential_not_authorized", "인증에 필요한 데이터가 없거나 유효하지 않습니다"),
    INTERNAL_AUTHENTICATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "internal_authentication_fail", "서버의 문제로 인증을 처리하지 못했습니다"),
    PASSWORD_RESET_EMAIL_VERIFY_FAIL(HttpStatus.BAD_REQUEST, "password_reset_email_verify_fail","유효하지 않거나 만료된 링크입니다"),

    GOOGLE_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "google_login_fail", "구글 로그인 요청에 실패했습니다"),
    KAKAO_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "kakao_login_fail","카카오 로그인 요청에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
