package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostBookmarkCancelRecord;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface MemberCancelPostBookmarkRecordTestUtils {
    MemberPostBookmarkCancelRecord testMemberPostBookmarkCancelRecord = new MemberPostBookmarkCancelRecord(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
