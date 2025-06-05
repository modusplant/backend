package kr.modusplant.domains.communication.conversation.app.http.response;

import java.util.UUID;

public record ConvCommentResponse(
        String postUlid,
        String path,
        UUID memberUuid,
        String nickname,
        String content
) {
}