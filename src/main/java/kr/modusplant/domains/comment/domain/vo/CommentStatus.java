package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.exception.enums.CommentStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor
public class CommentStatus {
    private String status;

    public static CommentStatus create(String status) {

        if(!CommentStatusType.isValidStatus(status)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_STATUS);
        }
        return new CommentStatus(status);
    }

    public static CommentStatus setAsValid() { return new CommentStatus("valid"); }
    public static CommentStatus setAsDeleted() { return new CommentStatus("deleted"); }

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
