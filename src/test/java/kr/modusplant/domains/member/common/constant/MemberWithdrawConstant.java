package kr.modusplant.domains.member.common.constant;

import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberWithdrawConstant {
    public static final MemberWithdrawReason MEMBER_WITHDRAW_BASIC_USER_REASON = MemberWithdrawReason.UNCOMFORTABLE_TO_USE;
    public static final String MEMBER_WITHDRAW_BASIC_USER_OPINION = "탈퇴하면서 남기는 의견";
}