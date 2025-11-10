package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberNickname;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface MemberNicknameTestUtils {
    MemberNickname testMemberNickname = MemberNickname.create(MEMBER_BASIC_USER_NICKNAME);
}
