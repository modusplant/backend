package kr.modusplant.domains.tip.app.http.request;

import java.util.UUID;

public record TipCommentInsertRequest(
        String postUlid,
        String path,
        UUID createMemberUuid,
        String content
) {}
