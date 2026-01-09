package kr.modusplant.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements kr.modusplant.shared.exception.enums.supers.ErrorCode {

    // -- common errors --
    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal_server_error", "서버에 문제가 발생했습니다"),

    INPUT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST.value(), "mismatch_input_type", "입력값의 서식이 올바르지 않습니다"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST.value(), "invalid_input", "입력값이 유효하지 않습니다"),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST.value(), "constraint_violation", "데이터에 설정된 조건을 위배했습니다"),
    MALFORMED_INPUT(HttpStatus.BAD_REQUEST.value(), "malformed_input", "입력값의 형식이 올바르지 않습니다"),
    UNEXPECTED_INPUT(HttpStatus.BAD_REQUEST.value(), "unexpected_input", "서버가 알 수 없는 입력값이 발견되었습니다"),

    INVALID_STATE(HttpStatus.CONFLICT.value(), "invalid_state", "리소스의 상태가 유효하지 않습니다"),

    // -- business errors --
    // exists and not found
    NICKNAME_EXISTS(HttpStatus.CONFLICT.value(), "nickname_exists", "닉네임이 이미 존재합니다"),
    MEMBER_EXISTS(HttpStatus.CONFLICT.value(), "member_exists", "사용자의 계정이 이미 존재합니다"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    MEMBER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "member_profile_not_found", "사용자의 프로필 정보가 존재하지 않습니다"),
    MEMBER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "comment_not_found", "댓글을 찾을 수 없습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "post_not_found", "게시글을 찾을 수 없습니다"),

    // empty or invalid value
    CONTENT_TYPE_EMPTY(HttpStatus.BAD_REQUEST.value(), "content_type_empty", "컨텐츠 타입이 비었습니다"),
    EMAIL_EMPTY(HttpStatus.BAD_REQUEST.value(), "empty_email","이메일이 비었습니다"),
    FILE_NAME_EMPTY(HttpStatus.BAD_REQUEST.value(), "file_name_empty", "파일명이 비었습니다"),
    NICKNAME_EMPTY(HttpStatus.BAD_REQUEST.value(), "nickname_empty", "닉네임이 비어 있습니다"),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST.value(), "password_empty", "비밀번호가 비어 있습니다"),

    FORBIDDEN_EMAIL(HttpStatus.FORBIDDEN.value(), "forbidden_email", "해당 이메일에 접근할 수 없습니다"),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.FORBIDDEN.value(), "invalid_email_verify_code", "이메일의 검증 코드가 올바르지 않습니다"),

    INVALID_EMAIL(HttpStatus.BAD_REQUEST.value(), "invalid_email", "이메일이 올바르지 않습니다"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST.value(), "invalid_nickname", "닉네임이 올바르지 않습니다"),
    INVALID_PAGE_RANGE(HttpStatus.BAD_REQUEST.value(), "invalid_page_range", "이용할 수 있는 페이지 범위가 아닙니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "invalid_password", "비밀번호가 올바르지 않습니다"),
    INVALID_FILE_INPUT(HttpStatus.BAD_REQUEST.value(),"invalid_file_input","파일 입력이 올바르지 않습니다"),

    // others
    UNSUPPORTED_FILE(HttpStatus.FORBIDDEN.value(), "unsupported_file", "지원되지 않는 파일 타입입니다"),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.FORBIDDEN.value(), "unsupported_social_provider", "지원되지 않는 소셜 로그인 방식입니다"),

    FILE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST.value(),"file_limit_exceeded","파일 개수 또는 크기 제한을 초과했습니다"),

    SPECIFIED_SORTING_METHOD(HttpStatus.BAD_REQUEST.value(), "specified_sorting_method", "페이지 정렬 방식은 지정되지 않아야 합니다"),

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
