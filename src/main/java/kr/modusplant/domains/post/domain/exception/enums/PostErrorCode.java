package kr.modusplant.domains.post.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ResponseCode {
    EMPTY_POST_ID(HttpStatus.BAD_REQUEST, "empty_post_id", "게시글 id가 비어 있습니다. "),
    INVALID_POST_ID(HttpStatus.BAD_REQUEST, "invalid_post_id", "게시글 id가 유효하지 않습니다. "),
    EMPTY_AUTHOR_ID(HttpStatus.BAD_REQUEST, "empty_author_id", "작성자 id가 비어 있습니다. "),
    INVALID_AUTHOR_ID(HttpStatus.BAD_REQUEST, "invalid_author_id", "작성자 id가 유효하지 않습니다. "),
    EMPTY_POST_CONTENT(HttpStatus.BAD_REQUEST, "empty_post_content", "게시글 컨텐츠가 비어 있습니다. "),
    INVALID_POST_CONTENT(HttpStatus.BAD_REQUEST, "invalid_post_content", "게시글 컨텐츠가 유효하지 않습니다. "),
    EMPTY_LIKE_COUNT(HttpStatus.BAD_REQUEST,"empty_like_count", "좋아요 수가 비어 있습니다. "),
    INVALID_LIKE_COUNT(HttpStatus.BAD_REQUEST, "invalid_like_count", "좋아요 수가 유효하지 않습니다. "),
    EMPTY_POST_STATUS(HttpStatus.BAD_REQUEST,"empty_post_status", "게시글 상태가 비어 있습니다. "),
    INVALID_POST_STATUS(HttpStatus.BAD_REQUEST, "invalid_post_status", "게시글 상태가 유효하지 않습니다. "),
    EMPTY_CATEGORY_ID(HttpStatus.BAD_REQUEST, "empty_category_id", "카테고리 id가 비어 있습니다. "),
    INVALID_CATEGORY_ID(HttpStatus.BAD_REQUEST, "invalid_category_id", "카테고리 id가 유효하지 않습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
