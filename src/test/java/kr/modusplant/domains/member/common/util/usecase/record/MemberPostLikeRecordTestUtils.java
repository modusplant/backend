package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostLikeRecord;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberPostLikeRecordTestUtils {
    MemberPostLikeRecord TEST_MEMBER_POST_LIKE_DTO = new MemberPostLikeRecord(TEST_MEMBER_ID_UUID, TEST_TARGET_POST_ID_STRING);
}
