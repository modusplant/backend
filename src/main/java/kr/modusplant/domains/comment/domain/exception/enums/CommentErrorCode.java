package kr.modusplant.domains.comment.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    EMPTY_POST_ID(HttpStatus.BAD_REQUEST.value(), "empty_post_id", "게시글의 식별자 값이 비었습니다"),
    EMPTY_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "empty_comment_path", "댓글의 경로 값이 비었습니다"),
    EMPTY_AUTHOR(HttpStatus.BAD_REQUEST.value(), "empty_author", "작성자의 값이 비어 있습니다"),
    EMPTY_COMMENT_CONTENT(HttpStatus.BAD_REQUEST.value(), "empty_comment_content", "댓글의 내용이 비어 있습니다"),
    EMPTY_COMMENT_STATUS(HttpStatus.BAD_REQUEST.value(), "empty_comment_status", "댓글의 상태 값이 비어 있습니다"),
    EMPTY_MEMBER_NICKNAME(HttpStatus.BAD_REQUEST.value(), "empty_member_nickname", "작성자의 닉네임 값이 비어 있습니다"),

    INVALID_POST_ID(HttpStatus.BAD_REQUEST.value(), "invalid_post_id", "게시글 식별자의 형식이 올바르지 않습니다"),
    INVALID_COMMENT_CONTENT(HttpStatus.BAD_REQUEST.value(), "invalid_comment_content", "댓글의 길이가 600자를 초과했습니다"),
    INVALID_COMMENT_PATH_FORMAT(HttpStatus.BAD_REQUEST.value(), "invalid_comment_path_format", "댓글 경로의 형식이 올바르지 않습니다"),
    INVALID_COMMENT_PATH_INDEX(HttpStatus.BAD_REQUEST.value(), "invalid_comment_path_index", "댓글 경로가 1 기반 인덱스를 따르지 않습니다"),
    INVALID_COMMENT_STATUS(HttpStatus.BAD_REQUEST.value(), "invalid_comment_status", "댓글의 상태가 올바르지 않습니다"),
    INVALID_AUTHOR_NICKNAME(HttpStatus.BAD_REQUEST.value(), "invalid_author_nickname", "작성자의 닉네임 형식이 올바르지 않습니다"),

    NOT_EXIST_AUTHOR(HttpStatus.NOT_FOUND.value(), "not_exist_author", "댓글 작성자의 데이터가 없습니다"),
    NOT_EXIST_POST(HttpStatus.NOT_FOUND.value(), "not_exist_post", "댓글이 작성된 게시글의 데이터를 찾을 수 없습니다"),
    NOT_EXIST_PARENT_COMMENT(HttpStatus.NOT_FOUND.value(), "not_exist_parent_comment", "댓글의 부모 댓글의 데이터가 없습니다"),
    NOT_EXIST_SIBLING_COMMENT(HttpStatus.NOT_FOUND.value(), "not_exist_sibling_comment", "댓글의 형제 댓글의 데이터가 없습니다"),

    EXIST_COMMENT(HttpStatus.CONFLICT.value(), "exist_comment", "댓글이 이미 존재합니다"),
    EXIST_POST_COMMENT(HttpStatus.CONFLICT.value(), "exist_post_comment", "게시글에 하나 이상의 댓글이 존재합니다");

    private final int httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
