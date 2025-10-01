package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberIdException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

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
            throw new EmptyMemberIdException();
        }
        return new MemberId(uuid);
    }

    public static MemberId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyMemberIdException();
        }
        if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidDataException(ErrorCode.INVALID_INPUT, "memberId");
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
