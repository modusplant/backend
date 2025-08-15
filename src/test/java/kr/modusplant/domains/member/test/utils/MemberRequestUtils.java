package kr.modusplant.domains.member.test.utils;

import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;

import static kr.modusplant.domains.member.test.vo.MemberStringVO.TEST_NICKNAME;

public interface MemberRequestUtils {
    MemberRegisterRequest testMemberRegisterRequest = new MemberRegisterRequest(TEST_NICKNAME);
}
