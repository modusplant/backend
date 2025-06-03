package kr.modusplant.domains.communication.qna.app.http.response;

import java.util.UUID;

public record QnaCommentResponse(
        String postUlid,
        String path,
        UUID authMemberUuid,
        UUID createMemberUuid,
        String content
        ) {}
