package kr.modusplant.domains.member.common.util.domain.entity;

import kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageBytesTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;

public interface MemberProfileImageTestUtils extends MemberProfileImagePathTestUtils, MemberProfileImageBytesTestUtils {
    MemberProfileImage testMemberProfileImage = MemberProfileImage.create(testMemberProfileImagePath, testMemberProfileImageBytes);
}
