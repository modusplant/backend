package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_URL;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberProfileResponseTestUtils {
    MemberProfileResponse testMemberProfileResponseV1 = new MemberProfileResponse(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_IMAGE_URL, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME);
    MemberProfileResponse testMemberProfileResponseV2 = new MemberProfileResponse(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME);
    MemberProfileResponse testMemberProfileResponseWithoutImage = new MemberProfileResponse(MEMBER_BASIC_USER_UUID, null, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME);
}
