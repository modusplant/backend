package kr.modusplant.domains.conversation.common.util.app.http.request;

import kr.modusplant.domains.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.domains.term.app.http.request.TermUpdateRequest;

public interface ConvCommentRequestTestUtils {
    ConvCommentInsertRequest convCommentInsertRequest = new ConvCommentInsertRequest(
            "01H5Z7XQ3W4F9K2G1V8R6T0Y5P", "/1/6/2", "테스트 댓글 내용");
}
