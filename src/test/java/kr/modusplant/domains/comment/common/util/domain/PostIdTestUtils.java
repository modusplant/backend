package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.PostId;

import static kr.modusplant.shared.persistence.common.constant.CommPostConstant.TEST_COMM_POST_ULID;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(TEST_COMM_POST_ULID);
}
