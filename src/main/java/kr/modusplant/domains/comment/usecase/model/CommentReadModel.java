package kr.modusplant.domains.comment.usecase.model;

public record CommentReadModel(
        String postUlid,
        String path,
        String authMemberUuid,
        String content,
        boolean isDeleted,
        String createdAt
) {
}
