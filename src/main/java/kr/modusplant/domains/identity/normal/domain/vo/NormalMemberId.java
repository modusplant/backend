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
public class NormalMemberId {
    private final UUID value;

    public static NormalMemberId create(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(NormalIdentityErrorCode.EMPTY_MEMBER_ID);
        }
        return new NormalMemberId(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof NormalMemberId normalMemberId)) return false;

        return new EqualsBuilder().append(getValue(), normalMemberId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
