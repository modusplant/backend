package kr.modusplant.domains.member.common.utils.domain;

import kr.modusplant.domains.member.domain.aggregate.Member;

public interface MemberTestUtils extends MemberIdTestUtils, MemberStatusTestUtils, NicknameTestUtils, BirthDateTestUtils {
    default Member createMember() {
        return Member.create(testMemberId, testMemberActiveStatus, TEST_MEMBER_NICKNAME, TEST_MEMBER_BIRTH_DATE);
    }
}
