package kr.modusplant.shared.persistence.common.util.constant;

import kr.modusplant.domains.member.domain.enums.WithdrawReason;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberWithdrawConstant {
    public static final WithdrawReason MEMBER_WITHDRAW_BASIC_USER_REASON = WithdrawReason.UNCOMFORTABLE_TO_USE;
    public static final String MEMBER_WITHDRAW_BASIC_USER_OPINION = "탈퇴하면서 남기는 의견";
}