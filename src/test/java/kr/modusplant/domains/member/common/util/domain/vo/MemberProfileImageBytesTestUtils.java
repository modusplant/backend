package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES;

public interface MemberProfileImageBytesTestUtils {
    MemberProfileImageBytes testMemberProfileImageBytes = MemberProfileImageBytes.create(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);
}
