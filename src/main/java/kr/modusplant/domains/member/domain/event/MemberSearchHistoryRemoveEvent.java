package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSearchHistoryRemoveEvent {
    private final UUID memberId;

    public static MemberSearchHistoryRemoveEvent create(UUID memberId) {
        if (memberId == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "memberId");
        } else {
            return new MemberSearchHistoryRemoveEvent(memberId);
        }
    }
}
