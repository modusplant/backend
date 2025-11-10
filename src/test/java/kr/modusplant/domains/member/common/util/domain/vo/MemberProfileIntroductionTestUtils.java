package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberProfileIntroductionTestUtils {
    MemberProfileIntroduction testMemberProfileIntroduction = MemberProfileIntroduction.create(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
}
