package kr.modusplant.domains.member.domain.event;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
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
            throw new InvalidValueException(MemberErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else {
            return new RecentlyViewMemberRemoveEvent(memberId);
        }
    }
}
