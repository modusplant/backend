package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberWithdrawalRecord;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;

public interface MemberWithdrawalRecordTestUtils {
    MemberWithdrawalRecord testMemberWithdrawalRecord = new MemberWithdrawalRecord(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
}
