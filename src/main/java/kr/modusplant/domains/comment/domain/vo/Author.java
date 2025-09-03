package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Author {
    private UUID memberUuid;
    // TODO: active user 인지 create user 인지 판단하는 로직을 넣어야 할까?

    public static Author create(UUID memberUuid) {
        if(memberUuid == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR); }
        return new Author(memberUuid);
    }

}
