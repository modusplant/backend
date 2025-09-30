package kr.modusplant.domains.comment.common.util.framework;

import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;

public interface CommentCompositeKeyTestUtils extends PostIdTestUtils, CommentPathTestUtils {
    CommentCompositeKey testCommentCompositeKey = CommentCompositeKey.builder()
            .postUlid(testPostId.getId())
            .path(testCommentPath.getPath())
            .build();
}
