package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberNicknameUpdateRecord;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberNicknameUpdateRecordTestUtils {
    MemberNicknameUpdateRecord TEST_MEMBER_NICKNAME_UPDATE_RECORD = new MemberNicknameUpdateRecord(TEST_MEMBER_ID_UUID, TEST_MEMBER_NICKNAME_STRING);
}
