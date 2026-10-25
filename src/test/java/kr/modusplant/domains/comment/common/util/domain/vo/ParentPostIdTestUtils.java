package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.vo.ParentPostId;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface ParentPostIdTestUtils {
    ParentPostId testParentPostId = ParentPostId.create(TEST_POST_ULID);
}
