package kr.modusplant.domains.conversation.app.http.response;

import java.util.UUID;

public record ConvCommentResponse(
        String postUlid,
        String materializedPath,
        UUID authMemberUuid,
        UUID createMemberUuid,
        String content
        ) {}
