package kr.modusplant.domains.member.test.utils;

import kr.modusplant.domains.member.domain.entity.Member;

public interface MemberUtils extends MemberIdUtils, MemberStatusUtils, NicknameUtils, BirthDateUtils {
    default Member createMember() {
        return Member.create(testMemberId, testMemberActiveStatus, testNickname, testBirthDate);
    }
}
