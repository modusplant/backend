package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeEvent {
    private final UUID memberId;
    private final String postUlid;

    public static PostLikeEvent create(UUID memberId, String postUlid) {
        if (memberId == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "memberId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        }
        return new PostLikeEvent(memberId, postUlid);
    }
}
