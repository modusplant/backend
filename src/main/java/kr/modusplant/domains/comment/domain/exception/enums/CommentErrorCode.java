package kr.modusplant.domains.comment.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ResponseCode {

    EMPTY_POST_ID(HttpStatus.BAD_REQUEST, "empty_post_id", "게시글의 식별자 값이 비었습니다"),
    EMPTY_COMMENT_PATH(HttpStatus.BAD_REQUEST, "empty_comment_path", "댓글의 경로 값이 비었습니다"),
    EMPTY_AUTHOR(HttpStatus.BAD_REQUEST, "empty_author", "작성자의 값이 비어 있습니다"),
    EMPTY_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "empty_comment_content", "댓글의 내용이 비어 있습니다"),
    EMPTY_MEMBER_NICKNAME(HttpStatus.BAD_REQUEST, "empty_member_nickname", "작성자의 닉네임 값이 비어 있습니다"),

    INVALID_COMMENT_PATH(HttpStatus.BAD_REQUEST, "invalid_comment_path", "댓글 경로의 형식이 올바르지 않습니다"),
    INVALID_COMMENT_STATUS(HttpStatus.BAD_REQUEST, "invalid_comment_status", "댓글의 상태가 올바르지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
