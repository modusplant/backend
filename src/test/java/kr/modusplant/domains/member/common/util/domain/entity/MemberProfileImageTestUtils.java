package kr.modusplant.domains.member.common.util.domain.entity;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageBytesTestUtils.testMemberProfileImageBytes;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImagePathTestUtils.testMemberProfileImagePath;

public interface MemberProfileImageTestUtils {
    MemberProfileImage testMemberProfileImage = MemberProfileImage.create(testMemberProfileImagePath, testMemberProfileImageBytes);
}
