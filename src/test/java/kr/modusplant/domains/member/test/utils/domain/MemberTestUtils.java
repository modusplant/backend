package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.entity.Member;

public interface MemberTestUtils extends MemberIdTestUtils, MemberStatusTestUtils, NicknameTestUtils, BirthDateTestUtils {
    default Member createMember() {
        return Member.create(testMemberId, testMemberActiveStatus, testNickname, testBirthDate);
    }
}
