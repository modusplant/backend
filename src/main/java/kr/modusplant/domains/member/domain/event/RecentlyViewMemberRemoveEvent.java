package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RecentlyViewMemberRemoveEvent {
    private final UUID memberId;

    public static RecentlyViewMemberRemoveEvent create(UUID memberId) {
        if (memberId == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "memberId");
        } else {
            return new RecentlyViewMemberRemoveEvent(memberId);
        }
    }
}
