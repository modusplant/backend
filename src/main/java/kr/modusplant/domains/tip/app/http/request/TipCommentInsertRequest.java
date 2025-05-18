package kr.modusplant.domains.tip.app.http.request;

public record TipCommentInsertRequest(
        String postUlid,
        String materializedPath,
        String content
) {}
