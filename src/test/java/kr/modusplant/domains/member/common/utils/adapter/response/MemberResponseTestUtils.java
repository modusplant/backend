package kr.modusplant.domains.member.common.utils.adapter.response;

import kr.modusplant.domains.member.usecase.response.MemberResponse;

import static kr.modusplant.domains.member.common.constant.MemberLocalDateConstant.TEST_MEMBER_BIRTHDATE;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_ACTIVE_STATUS;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_UUID;

public interface MemberResponseTestUtils {
    MemberResponse testMemberResponse = new MemberResponse(TEST_MEMBER_UUID, TEST_MEMBER_ACTIVE_STATUS, TEST_MEMBER_NICKNAME, TEST_MEMBER_BIRTHDATE);
}
