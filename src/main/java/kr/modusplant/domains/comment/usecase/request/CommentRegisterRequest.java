package kr.modusplant.domains.comment.usecase.request;

import java.util.UUID;

public record CommentRegisterRequest(
        String postId,
        String path,
        UUID memberUuid,
        String content
) {
}
