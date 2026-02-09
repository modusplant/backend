package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBookmarkCancelEvent {
    private final UUID memberId;
    private final String postId;

    public static PostBookmarkCancelEvent create(UUID memberId, String postId) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (postId.isEmpty()) {
            throw new InvalidValueException(ErrorCode.POST_NOT_FOUND, "postId");
        } else {
            return new PostBookmarkCancelEvent(memberId, postId);
        }
    }
}
