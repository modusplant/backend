package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberProfileImageFileName;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME_2;

public interface MemberProfileImageFileNameTestUtils {
    MemberProfileImageFileName testMemberProfileImageFileName = MemberProfileImageFileName.create(MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME);
    MemberProfileImageFileName testMemberProfileImageFileName2 = MemberProfileImageFileName.create(MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME_2);
}
