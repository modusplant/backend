package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.MemberResponse;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_ACTIVE_STATUS_STRING;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.*;

public interface MemberResponseTestUtils {
    MemberResponse testMemberResponse = new MemberResponse(MEMBER_BASIC_USER_UUID, MEMBER_BASIC_USER_ACTIVE_STATUS_STRING, MEMBER_BASIC_USER_NICKNAME, MEMBER_BASIC_USER_BIRTH_DATE);
}
