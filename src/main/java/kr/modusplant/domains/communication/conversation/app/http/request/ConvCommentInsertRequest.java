package kr.modusplant.domains.communication.conversation.app.http.request;

import java.util.UUID;

public record ConvCommentInsertRequest(
        String postUlid,
        String path,
        UUID createMemberUuid,
        String content
) {}
