package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeNotificationEvent {
    private final UUID actorId;
    private final String postUlid;

    public static PostLikeNotificationEvent create(UUID actorId, String postUlid) {
        if (actorId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_ACTOR, "actorId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST,"postUlid");
        }
        return new PostLikeNotificationEvent(actorId, postUlid);
    }
}
