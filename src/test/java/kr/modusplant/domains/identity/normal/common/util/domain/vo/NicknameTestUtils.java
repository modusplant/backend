package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.Nickname;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface NicknameTestUtils {
    Nickname testNickname = Nickname.create(MEMBER_BASIC_USER_NICKNAME);
}
