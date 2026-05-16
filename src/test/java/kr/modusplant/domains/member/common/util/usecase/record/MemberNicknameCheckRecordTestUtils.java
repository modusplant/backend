package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberNicknameCheckRecord;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface MemberNicknameCheckRecordTestUtils {
    MemberNicknameCheckRecord testMemberNicknameCheckRecord = new MemberNicknameCheckRecord(MEMBER_BASIC_USER_NICKNAME);
}
