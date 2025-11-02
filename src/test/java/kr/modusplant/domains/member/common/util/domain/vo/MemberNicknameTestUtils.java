package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberNickname;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;

public interface MemberNicknameTestUtils {
    MemberNickname testMemberNickname = MemberNickname.create(TEST_MEMBER_NICKNAME_STRING);
}
