package kr.modusplant.domains.conversation.app.http.request;

public record ConvCommentInsertRequest(
        String postUlid,
        String materializedPath,
        String content
) {}
