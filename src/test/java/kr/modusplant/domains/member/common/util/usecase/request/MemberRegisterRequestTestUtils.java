package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;

public interface MemberRegisterRequestTestUtils {
    MemberRegisterRequest testMemberRegisterRequest = new MemberRegisterRequest(TEST_MEMBER_NICKNAME_STRING);
}
