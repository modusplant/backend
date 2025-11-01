package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptySiteMemberTermIdException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SiteMemberTermId {
    private final UUID value;

    public static SiteMemberTermId fromUuid(UUID uuid) {
        if(uuid == null) {
            throw new EmptySiteMemberTermIdException();
        }
        return new SiteMemberTermId(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteMemberTermId siteMemberTermId)) return false;
        return new EqualsBuilder().append(getValue(), siteMemberTermId.getValue()).isEquals();
    }

    @Override
    public int hashCode() { return new HashCodeBuilder(17, 37).append(getValue()).toHashCode(); }
}
