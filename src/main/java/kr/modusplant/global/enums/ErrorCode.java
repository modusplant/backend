package kr.modusplant.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", "서버에 문제가 발생했습니다"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "invalid_input_value", "입력값이 올바르지 않습니다"),
    NO_INPUT_VALUE(HttpStatus.BAD_REQUEST, "no_input_value", "입력값이 존재하지 않습니다"),

    // business errors
    SITEMEMBER_EXISTS(HttpStatus.CONFLICT, "member_exists", "사용자의 계정이 이미 존재합니다"),
    SITEMEMBER_AUTH_EXISTS(HttpStatus.CONFLICT, "member_auth_exists", "사용자의 권한 정보가 이미 존재합니다"),
    SITEMEMBER_ROLE_EXISTS(HttpStatus.CONFLICT, "member_role_exists", "사용자의 역할 정보가 이미 존재합니다"),
    SITEMEMBER_TERM_EXISTS(HttpStatus.CONFLICT, "member_term_exists", "사용자가 동의한 약관 정보가 이미 존재합니다"),
    SITEMEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    SITEMEMBER_AUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "member_auth_not_found", "사용자의 권한 정보가 존재하지 않습니다"),
    SITEMEMBER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),
    SITEMEMBER_TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "member_term_not_found", "사용자가 동의한 약관 정보가 존재하지 않습니다"),

    TERM_EXISTS(HttpStatus.CONFLICT, "term_exists", "약관 정보가 이미 존재합니다"),
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "term_not_found", "약관 정보가 존재하지 않습니다"),

    CATEGORY_EXISTS(HttpStatus.CONFLICT, "category_exists", "항목이 이미 존재합니다"),
    COMMENT_EXISTS(HttpStatus.CONFLICT, "comment_exists", "댓글이 이미 존재합니다"),
    LIKE_EXISTS(HttpStatus.CONFLICT, "like_exists", "좋아요를 이미 눌렀습니다"),
    POST_EXISTS(HttpStatus.CONFLICT, "post_exists", "게시글이 이미 존재합니다"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "category_not_found", "항목을 찾을 수 없습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "comment_not_found", "댓글을 찾을 수 없습니다"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "like_not_found", "좋아요를 누르지 않았습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),

    EMPTY_POSTULID_INPUT(HttpStatus.BAD_REQUEST, "empty_postulid_input", "게시글 ulid의 값이 비어 있습니다"),
    EMPTY_PATH_INPUT(HttpStatus.BAD_REQUEST, "empty_path_input", "경로의 값이 비어 있습니다"),

    UNSUPPORTED_FILE(HttpStatus.NOT_ACCEPTABLE, "unsupported_file", "지원되지 않는 파일 타입입니다"),
    SPECIFIED_SORTING_METHOD(HttpStatus.BAD_REQUEST, "specified_sorting_method", "페이지 정렬 방식은 지정되지 않아야 합니다");

    // auth errors

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
