package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberNicknameTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;

public interface MemberTestUtils extends MemberIdTestUtils, MemberStatusTestUtils, MemberNicknameTestUtils, MemberBirthDateTestUtils {
    default Member createMember() {
        return Member.create(testMemberId, testMemberActiveStatus, testMemberNickname, testMemberBirthDate);
    }
}
