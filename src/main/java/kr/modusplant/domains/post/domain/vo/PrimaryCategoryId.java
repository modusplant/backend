package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimaryCategoryId {

    private final Integer value;

    public static PrimaryCategoryId create(Integer id) {
        if (id == null) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        }
        return new PrimaryCategoryId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PrimaryCategoryId primaryCategoryId)) return false;

        return new EqualsBuilder().append(getValue(), primaryCategoryId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
