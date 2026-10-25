package kr.modusplant.domains.comment.domain.entity;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.constant.Regex;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentAuthor {
    private final UUID uuid;
    private final String nickname;

    public static CommentAuthor create(UUID uuid) {
        if (uuid == null) { throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR, "uuid"); }
        return new CommentAuthor(uuid, null);
    }

    public static CommentAuthor createWithNullableUuid(UUID uuid) {
        return new CommentAuthor(uuid, null);
    }

    public static CommentAuthor create(UUID uuid, String nickname) {
        if (uuid == null) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_AUTHOR, "uuid");
        }
        if (nickname == null) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_MEMBER_NICKNAME, "nickname");
        }
        if (!nickname.matches(Regex.REGEX_NICKNAME)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_AUTHOR_NICKNAME, "nickname");
        }
        return new CommentAuthor(uuid, nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentAuthor commentAuthor)) return false;

        return new EqualsBuilder()
                .append(getUuid(), commentAuthor.getUuid())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getUuid()).toHashCode();
    }

}
