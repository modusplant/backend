package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberWithdrawalEvent {
    private final UUID memberId;

    public static MemberWithdrawalEvent create(UUID memberId) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else {
            return new MemberWithdrawalEvent(memberId);
        }
    }
}
