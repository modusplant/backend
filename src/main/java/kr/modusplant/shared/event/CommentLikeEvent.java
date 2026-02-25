package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentLikeEvent {
    private final UUID memberId;
    private final String postId;
    private final String path;

    public static CommentLikeEvent create(UUID memberId, String postId, String path) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (postId.isEmpty()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postId");
        } else if (path.isEmpty()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "path");
        } else {
            return new CommentLikeEvent(memberId, postId, path);
        }
    }
}
