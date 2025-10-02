package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
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
            throw new InvalidDataException(ErrorCode.MEMBER_NOT_FOUND, "memberId");
        } else if (postId.isEmpty()) {
            throw new InvalidDataException(ErrorCode.POST_NOT_FOUND, "postId");
        } else if (path.isEmpty()) {
            throw new InvalidDataException(ErrorCode.COMMENT_NOT_FOUND, "path");
        } else {
            return new CommentLikeEvent(memberId, postId, path);
        }
    }
}
