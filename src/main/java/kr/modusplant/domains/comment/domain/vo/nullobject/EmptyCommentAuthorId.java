package kr.modusplant.domains.comment.domain.vo.nullobject;

import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyCommentAuthorId extends CommentAuthorId {
    public static EmptyCommentAuthorId create() {
        return instance;
    }
    private static final EmptyCommentAuthorId instance = new EmptyCommentAuthorId();

    @Override
    public UUID getValue() {
        return null;
    }
}
