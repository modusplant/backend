package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.enums.CommentStatusType;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor
public class CommentStatus {
    private final CommentStatusType value;

    public static CommentStatus create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_STATUS, "status");
        }
        if (!CommentStatusType.contains(value)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_STATUS, "status");
        }
        return new CommentStatus(CommentStatusType.valueOf(value));
    }

    public static CommentStatus active() { return new CommentStatus(CommentStatusType.ACTIVE); }

    public static CommentStatus deleted() { return new CommentStatus(CommentStatusType.DELETED); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentStatus commentStatus)) return false;

        return new EqualsBuilder()
                .append(getValue(), commentStatus.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
