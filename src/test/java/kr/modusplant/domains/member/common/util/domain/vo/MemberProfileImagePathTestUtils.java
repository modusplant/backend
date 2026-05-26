package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;

public interface MemberProfileImagePathTestUtils {
    MemberProfileImagePath testMemberProfileImagePath = MemberProfileImagePath.create(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
}
