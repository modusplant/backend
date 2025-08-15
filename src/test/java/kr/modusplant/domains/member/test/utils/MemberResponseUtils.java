package kr.modusplant.domains.member.test.utils;

import kr.modusplant.domains.member.adapter.response.MemberResponse;

import static kr.modusplant.domains.member.test.vo.MemberLocalDateVO.TEST_BIRTHDATE;
import static kr.modusplant.domains.member.test.vo.MemberStringVO.TEST_MEMBER_ACTIVE_STATUS;
import static kr.modusplant.domains.member.test.vo.MemberStringVO.TEST_NICKNAME;
import static kr.modusplant.domains.member.test.vo.MemberUuidVO.TEST_MEMBER_UUID;

public interface MemberResponseUtils {
    MemberResponse testMemberResponse = new MemberResponse(TEST_MEMBER_UUID, TEST_MEMBER_ACTIVE_STATUS, TEST_NICKNAME, TEST_BIRTHDATE);
}
