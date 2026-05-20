package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;

public record MemberWithdrawalRecord(
        String authCode,
        String authProvider,
        MemberWithdrawReason reason,
        String opinion,
        String accessToken) {
}
