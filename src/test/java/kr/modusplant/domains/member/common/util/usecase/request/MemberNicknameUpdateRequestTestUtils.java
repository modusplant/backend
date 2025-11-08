package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberProfileOverrideRequest;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberNicknameUpdateRequestTestUtils {
    MemberProfileOverrideRequest TEST_MEMBER_NICKNAME_UPDATE_REQUEST = new MemberProfileOverrideRequest(MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_BASIC_USER_NICKNAME);
}
