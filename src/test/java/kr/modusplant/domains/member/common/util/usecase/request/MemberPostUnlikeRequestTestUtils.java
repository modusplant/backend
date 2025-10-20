package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberPostUnlikeRequest;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberPostUnlikeRequestTestUtils {
    MemberPostUnlikeRequest testMemberPostUnlikeRequest = new MemberPostUnlikeRequest(TEST_MEMBER_ID_UUID, TEST_TARGET_POST_ID_STRING);
}
