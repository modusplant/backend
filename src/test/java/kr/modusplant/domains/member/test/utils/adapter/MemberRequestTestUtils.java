package kr.modusplant.domains.member.test.utils.adapter;

import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;

import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_NICKNAME;

public interface MemberRequestTestUtils {
    MemberRegisterRequest testMemberRegisterRequest = new MemberRegisterRequest(TEST_NICKNAME);
}
