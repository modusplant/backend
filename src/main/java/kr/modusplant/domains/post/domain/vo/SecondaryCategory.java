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
public class SecondaryCategory {
    private final SecondaryCategoryId id;
    private final PrimaryCategoryId primaryCategoryId;
    private final String categoryName;
    private final int categoryOrder;

    public static SecondaryCategory create(SecondaryCategoryId id, PrimaryCategoryId primaryCategoryId, String categoryName, int categoryOrder) {
        if (id == null || primaryCategoryId == null) {
            throw new EmptyCategoryIdException();
        }  else if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new EmptyCategoryNameException();
        } else if (categoryOrder < 0) {
            throw new InvalidCategoryOrderException();
        }
        return new SecondaryCategory(id, primaryCategoryId, categoryName, categoryOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SecondaryCategory secondaryCategory)) return false;

        return new EqualsBuilder()
                .append(getId(), secondaryCategory.getId())
                .append(getPrimaryCategoryId(), secondaryCategory.getPrimaryCategoryId())
                .append(getCategoryName(),secondaryCategory.getCategoryName())
                .append(getCategoryOrder(),secondaryCategory.getCategoryOrder())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getPrimaryCategoryId())
                .append(getCategoryName())
                .append(getCategoryOrder())
                .toHashCode();
    }

}
