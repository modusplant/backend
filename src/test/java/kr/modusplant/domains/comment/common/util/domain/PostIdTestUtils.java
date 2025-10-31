package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.shared.persistence.common.util.constant.CommPostConstant;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(CommPostConstant.TEST_COMM_POST_ULID);
}
