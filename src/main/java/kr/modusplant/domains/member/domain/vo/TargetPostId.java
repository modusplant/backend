package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_TARGET_POST_ID;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_TARGET_POST_ID;
import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetPostId {
    private final String value;

    public static TargetPostId create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_TARGET_POST_ID, "targetPostId");
        } else if (!PATTERN_ULID.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_TARGET_POST_ID, "targetPostId");
        }
        return new TargetPostId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TargetPostId memberId)) return false;

        return new EqualsBuilder().append(getValue(), memberId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
