package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeEvent {
    private final UUID memberId;
    private final String postId;

    public static PostLikeEvent create(UUID memberId, String postId) {
        if (memberId == null) {
            throw new InvalidDataException(ErrorCode.MEMBER_NOT_FOUND, "memberId");
        } else if (postId.isEmpty()) {
            throw new InvalidDataException(ErrorCode.POST_NOT_FOUND, "postId");
        } else {
            return new PostLikeEvent(memberId, postId);
        }
    }
}
