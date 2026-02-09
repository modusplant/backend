package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_TARGET_COMMENT_PATH;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_TARGET_COMMENT_PATH;
import static kr.modusplant.shared.constant.Regex.PATTERN_MATERIALIZED_PATH;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetCommentPath {
    private final String value;

    public static TargetCommentPath create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_TARGET_COMMENT_PATH, "targetCommentPath");
        } else if (!PATTERN_MATERIALIZED_PATH.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_TARGET_COMMENT_PATH, "targetCommentPath");
        }
        return new TargetCommentPath(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TargetCommentPath memberId)) return false;

        return new EqualsBuilder().append(getValue(), memberId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
