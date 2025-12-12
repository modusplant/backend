package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.shared.kernel.Nickname;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface MemberNicknameTestUtils {
    Nickname TEST_NICKNAME = Nickname.create(MEMBER_BASIC_USER_NICKNAME);
}
