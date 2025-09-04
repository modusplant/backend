package kr.modusplant.domains.member.common.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.common.constant.MemberStringConstant;

public interface NicknameTestUtils {
    MemberNickname TEST_MEMBER_NICKNAME = MemberNickname.create(MemberStringConstant.TEST_MEMBER_NICKNAME);
}
