package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberCheckNicknameRecord;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface MemberCheckNicknameRecordTestUtils {
    MemberCheckNicknameRecord testMemberCheckNicknameRecord = new MemberCheckNicknameRecord(MEMBER_BASIC_USER_NICKNAME);
}
