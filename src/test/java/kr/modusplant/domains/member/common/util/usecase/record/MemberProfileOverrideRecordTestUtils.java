package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberProfileOverrideRecordTestUtils {
    MemberProfileOverrideRecord testMemberProfileOverrideRecord = new MemberProfileOverrideRecord(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_PROFILE_BASIC_USER_IMAGE, MEMBER_BASIC_USER_NICKNAME);
}
