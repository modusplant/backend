package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.MemberWithdrawalEvent;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_OPINION;
import static kr.modusplant.domains.member.common.constant.MemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_REASON;

public interface MemberWithdrawalEventTestUtils {
    MemberWithdrawalEvent testMemberWithdrawalEvent = MemberWithdrawalEvent.create(
            MEMBER_BASIC_USER_UUID, MEMBER_WITHDRAW_BASIC_USER_REASON.name(), MEMBER_WITHDRAW_BASIC_USER_OPINION);
}
