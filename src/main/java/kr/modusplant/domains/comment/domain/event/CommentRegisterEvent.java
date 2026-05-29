package kr.modusplant.domains.comment.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRegisterEvent {
    private final UUID actorId;
    private final String postUlid;
    private final String commentPath;
    private final String contentPreview;
    private final String action;

    public static CommentRegisterEvent create(UUID actorId, String postUlid, String commentPath, String contentPreview) {
        validate(actorId, postUlid, commentPath);
        return new CommentRegisterEvent(actorId, postUlid, commentPath, contentPreview, getActionType(commentPath));
    }

    private static void validate(UUID actorId, String postUlid, String commentPath) {
        if (actorId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_ACTOR, "actorId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postUlid");
        }
        if (commentPath == null || commentPath.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "commentPath");
        }
    }

    private static String getActionType(String commentPath) {
        return commentPath.contains(".") ? "COMMENT_REPLY_ADDED" : "COMMENT_ADDED";
    }
}
