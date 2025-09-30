package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.common.constant.MemberStringConstant;
import kr.modusplant.domains.member.domain.vo.MemberNickname;

public interface MemberNicknameTestUtils {
    MemberNickname testMemberNickname = MemberNickname.create(MemberStringConstant.TEST_MEMBER_NICKNAME);
}
