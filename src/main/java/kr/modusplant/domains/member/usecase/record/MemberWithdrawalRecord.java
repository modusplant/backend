package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import org.jspecify.annotations.NonNull;

import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

public record MemberWithdrawalRecord(
        String authCode,
        String authProvider,
        MemberWithdrawReason reason,
        String opinion,
        String accessToken) {
    @Override
    public @NonNull String toString() {
        return "MemberWithdrawalRecord[authCode=" + maskAnyString(authCode) +
                ", authProvider=" + authProvider +
                ", reason=" + reason +
                ", opinion=" + opinion +
                ", accessToken=" + maskAnyString(accessToken) +
                "]";
    }
}
