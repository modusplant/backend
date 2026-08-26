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

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentContent {
    private final String value;

    public static CommentContent create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT, "content");
        }
        if (value.length() > 600) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_CONTENT, "content");
        }
        return new CommentContent(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentContent commentContent)) return false;

        return new EqualsBuilder()
                .append(getValue(), commentContent.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
