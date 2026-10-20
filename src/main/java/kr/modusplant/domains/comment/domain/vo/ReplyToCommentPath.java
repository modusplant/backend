package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyToCommentPath {
    private final String value;

    public static ReplyToCommentPath create(String value) {
        if (StringUtils.isBlank(value)) {
            return EmptyReplyToCommentPath.create();
        }
        if (!value.matches("^\\d+(\\.\\d+)*$")) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH_FORMAT, "replyToPath");
        }
        if (value.charAt(0) == '0' || value.contains(".0")) { // value의 형식은 반드시 숫자와 점(.)의 연속물이어야 함
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH_INDEX, "replyToPath");
        }
        if (StringUtils.countMatches(value, ".") >= 2) { // 허용되는 최대 Depth는 2 (예: "1", "1.2")
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH_DEPTH, "replyToPath");
        }
        return new ReplyToCommentPath(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReplyToCommentPath replyToCommentPath)) return false;

        return new EqualsBuilder()
                .append(getValue(), replyToCommentPath.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
