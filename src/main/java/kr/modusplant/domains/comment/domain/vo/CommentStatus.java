package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.enums.CommentStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor
public class CommentStatus {
    private final CommentStatusType status;

    public static CommentStatus create(String status) {
        CommentStatus.validateSource(status);
        return new CommentStatus(CommentStatusType.valueOf(status));
    }

    public static void validateSource(String source) {
        if(source.isBlank()) { throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_STATUS); }

        if(!CommentStatusType.isValidStatus(source)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_STATUS);
        }
    }

    public static CommentStatus setAsValid() { return new CommentStatus(CommentStatusType.VALID); }
    public static CommentStatus setAsDeleted() { return new CommentStatus(CommentStatusType.DELETED); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentStatus commentStatus)) return false;

        return new EqualsBuilder()
                .append(getStatus(), commentStatus.getStatus())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getStatus()).toHashCode();
    }
}
