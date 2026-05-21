package kr.modusplant.domains.search.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {
    EMPTY_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST.value(), "empty_search_keyword", "검색 키워드가 비어 있습니다. "),
    EMPTY_SEARCH_KEYWORD_SIMILARITY(HttpStatus.BAD_REQUEST.value(), "empty_search_keyword_similarity", "검색 키워드 유사도가 비어 있습니다. "),
    EMPTY_SEARCH_POST_ID(HttpStatus.BAD_REQUEST.value(), "empty_search_post_id", "검색 게시글 아이디가 비어 있습니다. "),
    EMPTY_SEARCH_POST_IMPORTANCE(HttpStatus.BAD_REQUEST.value(), "empty_search_post_importance", "검색 게시글 중요도가 비어 있습니다. "),
    EMPTY_SEARCH_POST_OPTION(HttpStatus.BAD_REQUEST.value(), "empty_search_post_option", "검색 게시글 옵션이 비어 있습니다. "),
    EMPTY_SEARCH_POST_PUBLISHED_AT(HttpStatus.BAD_REQUEST.value(), "empty_search_post_published_at", "검색 게시글 발행 시점이 비어 있습니다. "),
    EMPTY_SEARCH_POST_SORT_CONDITION(HttpStatus.BAD_REQUEST.value(), "empty_search_post_sort_condition", "검색 게시글 정렬 조건이 비어 있습니다. "),
    EMPTY_SEARCH_POST_TARGET(HttpStatus.BAD_REQUEST.value(), "empty_search_post_target", "검색 게시글 대상이 비어 있습니다. "),

    INCORRECT_SEARCH_POST_OPTION(HttpStatus.BAD_REQUEST.value(), "incorrect_search_post_option", "검색 게시글 옵션이 올바르지 않습니다. "),
    INCORRECT_SEARCH_POST_CATEGORY_ID(HttpStatus.BAD_REQUEST.value(), "incorrect_search_post_category_id", "검색 게시글 항목 ID가 올바르지 않습니다. "),

    INVALID_SEARCH_POST_ID(HttpStatus.BAD_REQUEST.value(), "invalid_search_post_id", "검색 게시글 식별자의 서식이 올바르지 않습니다. "),

    NOT_FOUND_SEARCH_POST_IMPORTANCE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "not_found_search_post_importance", "검색 게시글 중요도를 찾을 수 없습니다. "),
    NOT_FOUND_SEARCH_POST_SIMILARITY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "not_found_search_post_similarity", "검색 게시글 유사도를 찾을 수 없습니다. "),

    SEARCH_POST_PUBLISHED_AT_AFTER_NOW(HttpStatus.BAD_REQUEST.value(), "search_post_published_at_after_now", "게시글 발행 시점이 현재 이후의 시간입니다. "),

    SEARCH_KEYWORD_SIMILARITY_OUT_OF_RANGE(HttpStatus.BAD_REQUEST.value(), "search_keyword_similarity_out_of_range", "검색 키워드 유사도가 올바른 값의 범위를 벗어났습니다. "),
    SEARCH_POST_IMPORTANCE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST.value(), "search_post_importance_out_of_range", "검색 게시글 중요도가 올바른 값의 범위를 벗어났습니다. ");

    private final int httpStatus;
    private final String code;
    private final String message;
}
