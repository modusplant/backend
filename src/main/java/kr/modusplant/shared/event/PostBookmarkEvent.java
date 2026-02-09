package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBookmarkEvent {
    private final UUID memberId;
    private final String postId;

    public static PostBookmarkEvent create(UUID memberId, String postId) {
        if (memberId == null) {
            throw new InvalidValueException(ErrorCode.MEMBER_NOT_FOUND, "memberId");
        } else if (postId.isEmpty()) {
            throw new InvalidValueException(ErrorCode.POST_NOT_FOUND, "postId");
        } else {
            return new PostBookmarkEvent(memberId, postId);
        }
    }
}
