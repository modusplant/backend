package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyTargetPostIdException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetPostId {
    private final String value;

    public static TargetPostId create(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyTargetPostIdException();
        } else if (!PATTERN_ULID.matcher(value).matches()) {
            throw new InvalidDataException(ErrorCode.INVALID_INPUT, "targetPostId");
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
