package kr.modusplant.domains.communication.conversation.app.http.response;

import java.util.UUID;

public record ConvCommentResponse(
        String postUlid,
        String path,
        UUID authMemberUuid,
        UUID createMemberUuid,
        String content
        ) {}
