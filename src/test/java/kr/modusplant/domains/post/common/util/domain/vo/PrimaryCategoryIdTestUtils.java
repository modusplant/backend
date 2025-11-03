package kr.modusplant.domains.post.common.util.domain.vo;

import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;

import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID;
import static kr.modusplant.domains.post.common.constant.PostUuidConstant.TEST_POST_UUID2;

public interface PrimaryCategoryIdTestUtils {
    PrimaryCategoryId testPrimaryCategoryId = PrimaryCategoryId.fromUuid(TEST_POST_UUID);
    PrimaryCategoryId testPrimaryCategoryId2 = PrimaryCategoryId.fromUuid(TEST_POST_UUID2);
}
