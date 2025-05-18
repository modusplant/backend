package kr.modusplant.domains.tip.common.util.app.http.request;

import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;

import java.util.UUID;

import static kr.modusplant.domains.tip.common.util.domain.TipPostTestUtils.tipPostWithUlid;

public interface TipCommentInsertRequestTestUtils {
    TipCommentInsertRequest tipCommentInsertRequest = new TipCommentInsertRequest(
            tipPostWithUlid.getUlid(), "/1/6/2",
            UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"), "테스트 댓글 내용");
}
