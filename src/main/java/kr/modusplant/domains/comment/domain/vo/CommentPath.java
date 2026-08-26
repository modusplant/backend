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
public class CommentPath {
    private final String value;

    public static CommentPath create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH, "path");
        }
        if (!value.matches("^\\d+(\\.\\d+)*$")) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH_FORMAT, "path");
        }
        if (value.charAt(0) == '0' || value.contains(".0")) { // value의 형식은 반드시 숫자와 점(.)의 연속물이어야 함
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH_INDEX, "path");
        }
        return new CommentPath(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentPath commentPath)) return false;

        return new EqualsBuilder()
                .append(getValue(), commentPath.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
