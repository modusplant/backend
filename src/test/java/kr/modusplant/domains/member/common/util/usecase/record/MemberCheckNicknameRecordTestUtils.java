package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberNicknameCheckRecord;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface MemberCheckNicknameRecordTestUtils {
    MemberNicknameCheckRecord TEST_MEMBER_NICKNAME_CHECK_RECORD = new MemberNicknameCheckRecord(MEMBER_BASIC_USER_NICKNAME);
}
