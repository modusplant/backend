package kr.modusplant.domains.post.common.utils.domain.vo;

import kr.modusplant.domains.post.domain.vo.PostId;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(TEST_POST_ULID);
}
