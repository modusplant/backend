package kr.modusplant.domains.member.test.utils.domain;

import kr.modusplant.domains.member.domain.aggregate.Member;

public interface MemberTestUtils extends MemberIdTestUtils, MemberStatusTestUtils, MemberNicknameTestUtils, MemberBirthDateTestUtils {
    default Member createMember() {
        return Member.create(testMemberId, testMemberActiveStatus, testMemberNickname, testMemberBirthDate);
    }
}
