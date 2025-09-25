package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;

public interface PostIdTestUtils extends CommPostTestUtils {
    PostId testPostId = PostId.create(TEST_COMM_POST_WITH_ULID.getUlid());
}
