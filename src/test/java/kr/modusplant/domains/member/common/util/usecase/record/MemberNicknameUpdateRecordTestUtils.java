package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberProfileUpdateRecord;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;

public interface MemberNicknameUpdateRecordTestUtils {
    MemberProfileUpdateRecord TEST_MEMBER_NICKNAME_UPDATE_RECORD = new MemberProfileUpdateRecord(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_PROFILE_BASIC_USER_IMAGE, MEMBER_BASIC_USER_NICKNAME);
}
