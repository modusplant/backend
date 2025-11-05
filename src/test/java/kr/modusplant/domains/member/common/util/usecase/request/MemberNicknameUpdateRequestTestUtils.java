package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberNicknameUpdateRequest;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;

public interface MemberNicknameUpdateRequestTestUtils {
    MemberNicknameUpdateRequest TEST_MEMBER_NICKNAME_UPDATE_REQUEST = new MemberNicknameUpdateRequest(TEST_MEMBER_NICKNAME_STRING);
}
