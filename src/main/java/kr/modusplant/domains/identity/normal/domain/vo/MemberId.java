package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberId {
    private final UUID value;

    public static MemberId create(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(NormalIdentityErrorCode.EMPTY_MEMBER_ID);
        }
        return new MemberId(uuid);
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
