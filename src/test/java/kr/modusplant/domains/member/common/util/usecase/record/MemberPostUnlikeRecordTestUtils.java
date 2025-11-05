package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostUnlikeRecord;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberPostUnlikeRecordTestUtils {
    MemberPostUnlikeRecord TEST_MEMBER_POST_UNLIKE_RECORD = new MemberPostUnlikeRecord(TEST_MEMBER_ID_UUID, TEST_TARGET_POST_ID_STRING);
}
