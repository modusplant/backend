package kr.modusplant.domains.member.common.utils.adapter.request;

import kr.modusplant.domains.member.usecase.request.MemberNicknameUpdateRequest;

import static kr.modusplant.domains.member.common.constant.MemberBooleanConstant.TEST_MEMBER_IS_ACTIVE;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_UUID;

public interface MemberRequestTestUtils {
    MemberNicknameUpdateRequest testMemberNicknameUpdateRequest = new MemberNicknameUpdateRequest(TEST_MEMBER_UUID, TEST_MEMBER_IS_ACTIVE, TEST_MEMBER_NICKNAME);
}
