package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentLikeEvent {
    private final UUID actorId;
    private final String postUlid;
    private final String commentPath;

    public static CommentLikeEvent create(UUID actorId, String postUlid, String commentPath) {
        if (actorId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_ACTOR, "actorId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postUlid");
        }
        if (commentPath == null || commentPath.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "commentPath");
        }
        return new CommentLikeEvent(actorId, postUlid, commentPath);
    }
}
