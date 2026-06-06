package kr.modusplant.domains.member.domain.event;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
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
            throw new InvalidValueException(MemberErrorCode.NOT_FOUND_MEMBER, "memberId");
        }
        if (postUlid == null || postUlid.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "postUlid");
        }
        if (commentPath == null || commentPath.isBlank()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "commentPath");
        }
        return new CommentLikeEvent(memberId, postUlid, commentPath);
    }
}
