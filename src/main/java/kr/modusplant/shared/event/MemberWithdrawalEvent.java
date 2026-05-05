package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberWithdrawalEvent {
    private final UUID memberId;
    private final String reason;
    private final String opinion;

    public static MemberWithdrawalEvent create(UUID memberId, String reason, String opinion) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (StringUtils.isBlank(reason)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER_WITHDRAW_REASON, "reason");
        } else {
            return new MemberWithdrawalEvent(memberId, reason, opinion);
        }
    }
}
