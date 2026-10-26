package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentLikeEvent {
    private final UUID memberId;
    private final String postUlid;
    private final String commentPath;

    public static CommentLikeEvent create(UUID memberId, String postUlid, String commentPath) {
        if (memberId == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "memberId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        }
        if (commentPath == null || commentPath.isBlank()) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "commentPath");
        }
        return new CommentLikeEvent(memberId, postUlid, commentPath);
    }
}
