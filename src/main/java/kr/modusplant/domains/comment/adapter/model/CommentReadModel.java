package kr.modusplant.domains.comment.adapter.model;

public record CommentReadModel(
        String postUlid,
        String path,
        String authMemberUuid,
        String createMemberUuid,
        String content,
        boolean isDeleted,
        String createdAt
) {
}
