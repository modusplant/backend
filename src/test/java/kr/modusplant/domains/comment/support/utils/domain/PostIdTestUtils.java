package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.vo.PostId;

import static kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils.TEST_COMM_POST_ULID;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(TEST_COMM_POST_ULID);
}
