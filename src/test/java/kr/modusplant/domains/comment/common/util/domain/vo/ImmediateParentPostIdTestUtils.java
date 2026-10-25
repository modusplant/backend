package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface ImmediateParentPostIdTestUtils {
    ImmediateParentPostId testImmediateParentPostId = ImmediateParentPostId.create(TEST_POST_ULID);
}
