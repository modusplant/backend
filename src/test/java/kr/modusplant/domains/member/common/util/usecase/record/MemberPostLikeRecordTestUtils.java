package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostLikeRecord;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface MemberPostLikeRecordTestUtils {
    MemberPostLikeRecord testMemberPostLikeRecord = new MemberPostLikeRecord(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
