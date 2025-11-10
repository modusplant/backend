package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptyTermIdException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TermId {
    private final UUID value;

    public static TermId generate() { return new TermId(UUID.randomUUID()); }

    public static TermId fromUuid(UUID uuid) {
        if(uuid == null) {
            throw new EmptyTermIdException();
        }
        return new TermId(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermId termId)) return false;
        return new EqualsBuilder().append(getValue(), termId.getValue()).isEquals();
    }

    @Override
    public int hashCode() { return new HashCodeBuilder(17, 37).append(getValue()).toHashCode(); }
}
