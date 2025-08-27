package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.MemberNickname;

import static kr.modusplant.domains.member.test.constant.MemberStringConstant.TEST_NICKNAME;

public interface NicknameTestUtils {
    MemberNickname TEST_MEMBER_NICKNAME = MemberNickname.of(TEST_NICKNAME);
}
