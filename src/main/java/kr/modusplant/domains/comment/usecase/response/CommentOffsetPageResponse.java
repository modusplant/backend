package kr.modusplant.domains.comment.usecase.response;

import java.util.List;

public record CommentOffsetPageResponse<T> (
        List<T> commentList,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {}
