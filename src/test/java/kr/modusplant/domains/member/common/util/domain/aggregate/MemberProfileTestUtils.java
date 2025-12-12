package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;

import static kr.modusplant.domains.member.common.util.domain.entity.MemberProfileImageTestUtils.testMemberProfileImage;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberNicknameTestUtils.TEST_NICKNAME;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;

public interface MemberProfileTestUtils {
    default MemberProfile createMemberProfile() {
        return MemberProfile.create(testMemberId, testMemberProfileImage, testMemberProfileIntroduction, TEST_NICKNAME);
    }
}
