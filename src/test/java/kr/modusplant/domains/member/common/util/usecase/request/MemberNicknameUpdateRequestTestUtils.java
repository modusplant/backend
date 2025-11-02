package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberNicknameUpdateRequest;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberNicknameUpdateRequestTestUtils {
    MemberNicknameUpdateRequest testMemberNicknameUpdateRequest = new MemberNicknameUpdateRequest(TEST_MEMBER_ID_UUID, TEST_MEMBER_NICKNAME_STRING);
}
