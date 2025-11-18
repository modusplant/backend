package kr.modusplant.domains.comment.usecase.response;

import java.util.List;

public record CommentPageResponse<T>(
        List<T> commentList,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
