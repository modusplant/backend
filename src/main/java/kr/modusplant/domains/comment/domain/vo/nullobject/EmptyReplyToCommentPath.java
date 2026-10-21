package kr.modusplant.domains.comment.domain.vo.nullobject;

import kr.modusplant.domains.comment.domain.vo.ReplyToCommentPath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyReplyToCommentPath extends ReplyToCommentPath {
    public static EmptyReplyToCommentPath create() {
        return instance;
    }
    private static final EmptyReplyToCommentPath instance = new EmptyReplyToCommentPath();

    @Override
    public String getValue() {
        return null;
    }
}
