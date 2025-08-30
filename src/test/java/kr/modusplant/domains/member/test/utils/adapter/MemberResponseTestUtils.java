package kr.modusplant.domains.member.test.utils.adapter;

import kr.modusplant.domains.member.adapter.response.MemberResponse;

import static kr.modusplant.domains.member.test.constant.MemberLocalDateConstant.TEST_MEMBER_BIRTHDATE;
import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_MEMBER_ACTIVE_STATUS;
import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_MEMBER_NICKNAME;
import static kr.modusplant.domains.member.test.constant.MemberUuidConstant.TEST_MEMBER_UUID;

public interface MemberResponseTestUtils {
    MemberResponse testMemberResponse = new MemberResponse(TEST_MEMBER_UUID, TEST_MEMBER_ACTIVE_STATUS, TEST_MEMBER_NICKNAME, TEST_MEMBER_BIRTHDATE);
}
