package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.exception.EmptyCategoryNameException;
import kr.modusplant.domains.post.domain.exception.InvalidCategoryOrderException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimaryCategory {
    private final PrimaryCategoryId id;
    private final String categoryName;
    private final int categoryOrder;

    public static PrimaryCategory create(PrimaryCategoryId id, String categoryName, int categoryOrder) {
        if (id == null) {
            throw new EmptyCategoryIdException();
        } else if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new EmptyCategoryNameException();
        } else if (categoryOrder < 0) {
            throw new InvalidCategoryOrderException();
        }
        return new PrimaryCategory(id, categoryName, categoryOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PrimaryCategory primaryCategory)) return false;

        return new EqualsBuilder().append(getId(), primaryCategory.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
    }

}
