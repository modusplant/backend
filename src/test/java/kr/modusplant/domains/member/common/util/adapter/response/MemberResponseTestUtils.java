package kr.modusplant.domains.member.common.util.adapter.response;

import kr.modusplant.domains.member.usecase.response.MemberResponse;

import static kr.modusplant.domains.member.common.constant.MemberLocalDateConstant.TEST_MEMBER_BIRTHDATE_LOCAL_DATE;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_ACTIVE_STATUS_STRING;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberResponseTestUtils {
    MemberResponse testMemberResponse = new MemberResponse(TEST_MEMBER_ID_UUID, TEST_MEMBER_ACTIVE_STATUS_STRING, TEST_MEMBER_NICKNAME_STRING, TEST_MEMBER_BIRTHDATE_LOCAL_DATE);
}
