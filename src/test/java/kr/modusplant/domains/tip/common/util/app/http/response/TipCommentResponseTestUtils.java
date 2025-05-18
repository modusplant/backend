package kr.modusplant.domains.tip.common.util.app.http.response;

import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;

import java.util.UUID;

public interface TipCommentResponseTestUtils {
    TipCommentResponse tipCommentResponse = new TipCommentResponse(
            "01H5Z7XQ3W4F9K2G1V8R6T0Y5P", "/1/6/2",
            UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd"), UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"),
             "테스트 댓글 내용"
    );
}
