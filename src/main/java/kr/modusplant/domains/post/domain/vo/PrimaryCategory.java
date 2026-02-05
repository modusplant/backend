package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
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
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        } else if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_NAME);
        } else if (categoryOrder < 0) {
            throw new InvalidValueException(PostErrorCode.INVALID_CATEGORY_ORDER);
        }
        return new PrimaryCategory(id, categoryName, categoryOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PrimaryCategory primaryCategory)) return false;

        return new EqualsBuilder()
                .append(getId(), primaryCategory.getId())
                .append(getCategoryName(), primaryCategory.getCategoryName())
                .append(getCategoryOrder(), primaryCategory.getCategoryOrder())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getCategoryName())
                .append(getCategoryOrder())
                .toHashCode();
    }

}
