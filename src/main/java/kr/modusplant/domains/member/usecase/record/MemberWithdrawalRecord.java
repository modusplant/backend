package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.domain.enums.WithdrawReason;

public record MemberWithdrawalRecord(
        String authCode,
        String authProvider,
        WithdrawReason reason,
        String opinion,
        String accessToken) {
}
