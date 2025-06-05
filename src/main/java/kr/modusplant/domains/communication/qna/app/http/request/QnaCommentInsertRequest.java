package kr.modusplant.domains.communication.qna.app.http.request;

import java.util.UUID;

public record QnaCommentInsertRequest(
        String postUlid,
        String path,
        UUID createMemberUuid,
        String content
) {}
