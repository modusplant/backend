package kr.modusplant.domains.comment.support.utils.framework;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.support.utils.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;

public interface CommentCompositeKeyTestUtils extends PostIdTestUtils, CommentPathTestUtils {
    CommentCompositeKey testCommentCompositeKey = CommentCompositeKey.builder()
            .postUlid(testPostId.getId())
            .path(testCommentPath.getPath())
            .build();
}
