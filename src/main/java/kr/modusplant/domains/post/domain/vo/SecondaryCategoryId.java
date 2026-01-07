package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SecondaryCategoryId {
    private final Integer value;

    public static SecondaryCategoryId create(Integer id) {
        if (id == null) {
            throw new EmptyCategoryIdException();
        }
        return new SecondaryCategoryId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SecondaryCategoryId secondaryCategoryId)) return false;

        return new EqualsBuilder().append(getValue(), secondaryCategoryId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
