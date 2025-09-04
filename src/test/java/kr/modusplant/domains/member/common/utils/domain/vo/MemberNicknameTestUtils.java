package kr.modusplant.domains.member.common.utils.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.common.constant.MemberStringConstant;

public interface MemberNicknameTestUtils {
    MemberNickname testMemberNickname = MemberNickname.create(MemberStringConstant.TEST_MEMBER_NICKNAME);
}
