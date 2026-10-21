package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode.COMMENT_PAGE_SIZE_OUT_OF_RANGE;
import static kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode.EMPTY_COMMENT_PAGE_SIZE;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentPageSize {
    private final int value;

    public static CommentPageSize create(Integer value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_COMMENT_PAGE_SIZE, "commentPageSize");
        } else if (value <= 0 || value > 50) {
            throw new InvalidValueException(COMMENT_PAGE_SIZE_OUT_OF_RANGE, "commentPageSize");
        }
        return new CommentPageSize(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentPageSize commentPageSize)) return false;

        return new EqualsBuilder().append(getValue(), commentPageSize.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
