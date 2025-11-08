package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberNicknameTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberProfileImageTestUtils;
import kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;

public interface MemberProfileTestUtils extends MemberIdTestUtils, MemberProfileImageTestUtils, MemberProfileIntroductionTestUtils, MemberNicknameTestUtils {
    default MemberProfile createMemberProfile() {
        return MemberProfile.create(testMemberId, testMemberProfileImage, testMemberProfileIntroduction, testMemberNickname);
    }
}
