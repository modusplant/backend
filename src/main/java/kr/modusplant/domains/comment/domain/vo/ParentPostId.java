package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ParentPostId {
    private final String value;

    public static ParentPostId create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID, "postId");
        }
        if (value.length() != 26) {
            throw new InvalidValueException(CommentErrorCode.INVALID_POST_ID, "postId");
        }
        if (!PATTERN_ULID.matcher(value).matches()) {
            throw new InvalidValueException(CommentErrorCode.INVALID_POST_ID, "postId");
        }
        return new ParentPostId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ParentPostId parentPostId)) return false;

        return new EqualsBuilder()
                .append(getValue(), parentPostId.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
