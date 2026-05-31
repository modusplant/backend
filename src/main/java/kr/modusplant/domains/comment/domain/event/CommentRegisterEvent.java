package kr.modusplant.domains.comment.domain.event;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRegisterEvent {
    private final UUID authorId;
    private final String postUlid;
    private final String commentPath;
    private final String contentPreview;
    private final String action;

    public static CommentRegisterEvent create(UUID authorId, String postUlid, String commentPath, String contentPreview) {
        validate(authorId, postUlid, commentPath);
        return new CommentRegisterEvent(authorId, postUlid, commentPath, contentPreview, getActionType(commentPath));
    }

    private static void validate(UUID actorId, String postUlid, String commentPath) {
        if (actorId == null) {
            throw new InvalidValueException(CommentErrorCode.EMPTY_AUTHOR, "authorId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "postUlid");
        }
        if (commentPath == null || commentPath.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "commentPath");
        }
    }

    private static String getActionType(String commentPath) {
        return commentPath.contains(".") ? "COMMENT_REPLY_ADDED" : "COMMENT_ADDED";
    }
}
