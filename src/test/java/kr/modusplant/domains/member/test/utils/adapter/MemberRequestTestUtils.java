package kr.modusplant.domains.member.test.utils.adapter;

import kr.modusplant.domains.member.adapter.in.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;

import static kr.modusplant.domains.member.test.constant.MemberBooleanConstant.TEST_MEMBER_IS_ACTIVE;
import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;
import static kr.modusplant.domains.member.test.constant.MemberUuidConstant.TEST_MEMBER_UUID;

public interface MemberRequestTestUtils {
    MemberRegisterRequest testMemberRegisterRequest = new MemberRegisterRequest(TEST_MEMBER_NICKNAME);
    MemberNicknameUpdateRequest testMemberNicknameUpdateRequest = new MemberNicknameUpdateRequest(TEST_MEMBER_UUID, TEST_MEMBER_IS_ACTIVE, TEST_MEMBER_NICKNAME);
}
