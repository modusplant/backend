package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyCommentAuthorId;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode.EMPTY_COMMENT_AUTHOR;
import static kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode.INVALID_COMMENT_AUTHOR_ID;
import static kr.modusplant.shared.constant.Regex.PATTERN_UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentAuthorId {
    private final UUID value;

    public static CommentAuthorId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(EMPTY_COMMENT_AUTHOR, "commentAuthorId");
        }
        return new CommentAuthorId(uuid);
    }

    public static CommentAuthorId fromNullableUuid(UUID uuid) {
        if (uuid == null) {
            return EmptyCommentAuthorId.create();
        }
        return new CommentAuthorId(uuid);
    }

    public static CommentAuthorId fromString(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_COMMENT_AUTHOR, "commentAuthorId");
        } else if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_COMMENT_AUTHOR_ID, "commentAuthorId");
        }
        return new CommentAuthorId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentAuthorId commentAuthorId)) return false;

        return new EqualsBuilder().append(getValue(), commentAuthorId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
