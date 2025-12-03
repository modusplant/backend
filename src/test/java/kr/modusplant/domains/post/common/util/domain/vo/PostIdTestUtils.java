package kr.modusplant.domains.post.common.util.domain.vo;

import kr.modusplant.domains.post.domain.vo.PostId;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;

public interface PostIdTestUtils {
    PostId testPostId = PostId.create(TEST_POST_ULID);
    PostId testPostId2 = PostId.create(TEST_POST_ULID2);
}
