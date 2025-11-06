package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberProfileUpdateRequest;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_URL;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberNicknameUpdateRequestTestUtils {
    MemberProfileUpdateRequest TEST_MEMBER_NICKNAME_UPDATE_REQUEST = new MemberProfileUpdateRequest(MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_PROFILE_BASIC_USER_IMAGE_URL, MEMBER_BASIC_USER_NICKNAME);
}
