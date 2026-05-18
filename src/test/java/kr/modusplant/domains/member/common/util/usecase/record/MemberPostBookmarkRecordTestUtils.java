package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostBookmarkRecord;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface MemberPostBookmarkRecordTestUtils {
    MemberPostBookmarkRecord testMemberPostBookmarkRecord = new MemberPostBookmarkRecord(MEMBER_BASIC_USER_UUID, TEST_POST_ULID);
}
