package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface MemberRegisterRequestTestUtils {
    MemberRegisterRequest testMemberRegisterRequest = new MemberRegisterRequest(MEMBER_BASIC_USER_NICKNAME);
}
