package kr.modusplant.domains.post.common.utils.domain.vo;

import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;

import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID;
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID2;

public interface SecondaryCategoryIdTestUtils {
    SecondaryCategoryId testSecondaryCategoryId = SecondaryCategoryId.fromUuid(TEST_POST_UUID);
    SecondaryCategoryId testSecondaryCategoryId2 = SecondaryCategoryId.fromUuid(TEST_POST_UUID2);
}
