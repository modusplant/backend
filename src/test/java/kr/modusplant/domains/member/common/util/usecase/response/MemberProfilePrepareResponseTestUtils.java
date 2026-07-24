package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.MemberProfilePrepareResponse;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL;

public interface MemberProfilePrepareResponseTestUtils {
    MemberProfilePrepareResponse testMemberProfilePrepareResponse = new MemberProfilePrepareResponse(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_PROFILE_BASIC_USER_IMAGE_STORAGE_URL);
}
