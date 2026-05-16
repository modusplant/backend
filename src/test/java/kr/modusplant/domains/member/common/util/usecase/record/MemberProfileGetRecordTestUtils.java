package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberProfileGetRecord;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberProfileGetRecordTestUtils {
    MemberProfileGetRecord testMemberProfileGetRecord = new MemberProfileGetRecord(MEMBER_BASIC_USER_UUID);
}
