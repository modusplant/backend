package kr.modusplant.domains.post.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

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
    INVALID_CATEGORY_ID(HttpStatus.BAD_REQUEST, "invalid_category_id", "카테고리 id가 유효하지 않습니다. "),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "post_access_denied", "게시글에 대한 접근 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다."),
    CONTENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"content_processing_failed","콘텐츠를 처리하는 중 오류가 발생했습니다."),
    EMPTY_CATEGORY_NAME(HttpStatus.BAD_REQUEST,"empty_category_name","카테고리 이름이 비어 있습니다. "),
    INVALID_CATEGORY_ORDER(HttpStatus.BAD_REQUEST,"invalid_category_order","카테고리 순서가 유효하지 않습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
