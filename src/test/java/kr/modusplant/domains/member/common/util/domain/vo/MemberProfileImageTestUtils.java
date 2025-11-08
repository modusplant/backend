package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;

public interface MemberProfileImageTestUtils extends MemberProfileImagePathTestUtils, MemberProfileImageBytesTestUtils{
    MemberProfileImage testMemberProfileImage = MemberProfileImage.create(testMemberProfileImagePath, testMemberProfileImageBytes);
}
