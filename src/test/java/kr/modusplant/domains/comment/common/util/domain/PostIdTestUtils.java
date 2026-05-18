package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.PostId;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(TEST_POST_ULID);
}
