package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {
    private final String id;

    public static PostId create(String ulid) {
        if (ulid == null || ulid.trim().isEmpty()) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_POST_ID);
        }
        if (!isValidUlid(ulid)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_POST_ID);
        }
        return new PostId(ulid);
    }

    private static boolean isValidUlid(String ulid) {
        if (StringUtils.isBlank(ulid) || ulid.length() != 26) {
            return false;
        }
        return PATTERN_ULID.matcher(ulid).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PostId postId)) return false;

        return new EqualsBuilder()
                .append(getId(), postId.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId()).toHashCode();
    }
}
