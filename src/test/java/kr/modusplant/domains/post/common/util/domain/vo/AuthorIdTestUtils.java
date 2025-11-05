package kr.modusplant.domains.post.common.util.domain.vo;

import kr.modusplant.domains.post.domain.vo.AuthorId;

import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID;
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID2;

public interface AuthorIdTestUtils {
    AuthorId testAuthorId = AuthorId.fromUuid(TEST_POST_UUID);
    AuthorId testAuthorId2 = AuthorId.fromUuid(TEST_POST_UUID2);
}
