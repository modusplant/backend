package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentPath {
    private final String path;

    public static CommentPath create(String path) {
        CommentPath.validateSource(path);
        return new CommentPath(path);
    }

    /**
     * @param source 의 형식은 반드시 숫자와 점(.)의 연속물이어야 합니다.
     */
    public static void validateSource(String source) {
        if (source.isBlank()) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH);
        }
        if (!source.matches("^\\d+(\\.\\d+)*$")) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentPath commentPath)) return false;

        return new EqualsBuilder()
                .append(getPath(), commentPath.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPath()).toHashCode();
    }
}
