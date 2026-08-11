package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.domains.search.domain.vo.nullobject.EmptySearcherId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearcherId {
    private final UUID value;

    public static SearcherId fromUuid(UUID value) {
        if (value == null) {
            return EmptySearcherId.create();
        }
        return new SearcherId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearcherId searcherId)) return false;

        return new EqualsBuilder().append(getValue(), searcherId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
