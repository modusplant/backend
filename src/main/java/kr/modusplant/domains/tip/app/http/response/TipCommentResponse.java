package kr.modusplant.domains.tip.app.http.response;

import java.util.UUID;

public record TipCommentResponse(
        String postUlid,
        String path,
        UUID authMemberUuid,
        UUID createMemberUuid,
        String content
        ) {}
