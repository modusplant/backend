package kr.modusplant.domains.comment.usecase.response;

import java.util.List;

public record CommentPageResponse<T>(
        List<T> commentList,
        int currentPage,
        int pageSize,
        long totalComments,
        int totalPages
) {
}
