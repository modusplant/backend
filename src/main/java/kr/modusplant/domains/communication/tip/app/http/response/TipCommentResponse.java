package kr.modusplant.domains.communication.tip.app.http.response;

import java.util.UUID;

public record TipCommentResponse(
        String postUlid,
        String path,
        UUID authMemberUuid,
        UUID createMemberUuid,
        String content
        ) {}
