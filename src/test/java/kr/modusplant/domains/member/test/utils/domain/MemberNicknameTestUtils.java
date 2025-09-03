package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.test.constant.MemberStringConstant;

public interface MemberNicknameTestUtils {
    MemberNickname testMemberNickname = MemberNickname.create(MemberStringConstant.TEST_MEMBER_NICKNAME);
}
