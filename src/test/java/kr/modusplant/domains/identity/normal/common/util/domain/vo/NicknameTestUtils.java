package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.NormalNickname;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface NicknameTestUtils {
    NormalNickname TEST_NORMAL_NICKNAME = NormalNickname.create(MEMBER_BASIC_USER_NICKNAME);
}
