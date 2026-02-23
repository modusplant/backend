package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_MEMBER_ID;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_MEMBER_ID;
import static kr.modusplant.shared.constant.Regex.PATTERN_UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberId {
    private final UUID value;

    public static MemberId generate() {
        return new MemberId(UUID.randomUUID());
    }

    public static MemberId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(EMPTY_MEMBER_ID, "memberId");
        }
        return new MemberId(uuid);
    }

    public static MemberId fromString(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_MEMBER_ID, "memberId");
        } else if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_MEMBER_ID, "memberId");
        }
        return new MemberId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberId memberId)) return false;

        return new EqualsBuilder().append(getValue(), memberId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
