package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.domain.aggregate.Member;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;

public interface MemberTestUtils {
    default Member createMember() {
        return Member.create(testMemberId, testMemberActiveStatus, testNormalUserNickname, testMemberBirthDate);
    }
}
