package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberRoleGetRecord;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface MemberRoleGetRecordTestUtils {
    MemberRoleGetRecord testMemberRoleGetRecord = new MemberRoleGetRecord(MEMBER_BASIC_USER_UUID);
}
