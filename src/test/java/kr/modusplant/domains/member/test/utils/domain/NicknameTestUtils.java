package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.vo.Nickname;

import static kr.modusplant.domains.member.test.vo.MemberStringVO.TEST_NICKNAME;

public interface NicknameTestUtils {
    Nickname testNickname = Nickname.of(TEST_NICKNAME);
}
