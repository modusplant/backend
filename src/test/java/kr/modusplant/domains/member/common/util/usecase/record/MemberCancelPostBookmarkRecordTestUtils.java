package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostBookmarkCancelRecord;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberCancelPostBookmarkRecordTestUtils {
    MemberPostBookmarkCancelRecord TEST_MEMBER_POST_BOOKMARK_CANCEL_RECORD = new MemberPostBookmarkCancelRecord(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
