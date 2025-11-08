package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberPostUnlikeRecord;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberPostUnlikeRecordTestUtils {
    MemberPostUnlikeRecord testMemberPostUnlikeRecord = new MemberPostUnlikeRecord(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);
}
