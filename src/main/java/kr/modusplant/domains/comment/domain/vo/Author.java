package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Author {
    private UUID memberUuid;
    private String memberNickname;

    public static Author create(UUID memberUuid) {
        if(memberUuid == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR); }
        return new Author(memberUuid, null);
    }

    public static Author create(UUID memberUuid, String memberNickname) {
        if(memberUuid == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR); }
        if(memberNickname == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_MEMBER_NICKNAME); }
        return new Author(memberUuid, memberNickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Author author)) return false;

        return new EqualsBuilder()
                .append(getMemberUuid(), author.getMemberUuid())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getMemberUuid()).toHashCode();
    }

}
