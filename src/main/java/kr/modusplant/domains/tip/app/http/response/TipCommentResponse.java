package kr.modusplant.domains.tip.app.http.response;

import java.util.UUID;

public record TipCommentResponse(
        String postUlid,
        String materializedPath,
        UUID authMemberUuid,
        UUID createMemberUuid,
        String content
        ) {}
