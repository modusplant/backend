package kr.modusplant.domains.member.common.util.adapter.response;

import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberProfileResponseTestUtils {
    MemberProfileResponse testMemberProfileResponse = new MemberProfileResponse(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME);
}
