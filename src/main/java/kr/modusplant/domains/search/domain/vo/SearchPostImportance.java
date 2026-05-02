package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPostImportance {
    private static final SearchPostImportance emptySearchPostImportance =
            new SearchPostImportance(0, true);

    private final int value;

    @Getter
    private final boolean empty;

    public static SearchPostImportance create(Integer value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_IMPORTANCE, "searchPostImportance");
        } else if (value < 1 || value > 4) {
            throw new InvalidValueException(SEARCH_POST_IMPORTANCE_OUT_OF_RANGE, "searchPostImportance");
        }
        return new SearchPostImportance(value, false);
    }

    public static SearchPostImportance createEmpty() {
        return emptySearchPostImportance;
    }

    public int getValueIfNotEmpty() {
        if (isEmpty()) {
            throw new InvalidValueException(NOT_FOUND_SEARCH_POST_IMPORTANCE, "searchPostImportance");
        }
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPostImportance searchPostImportance)) return false;

        if (isEmpty() && searchPostImportance.isEmpty()) return true;

        if (isEmpty() || searchPostImportance.isEmpty()) return false;

        return new EqualsBuilder().append(getValueIfNotEmpty(), searchPostImportance.getValueIfNotEmpty()).isEquals();
    }

    @Override
    public int hashCode() {
        if (isEmpty()) {
            return 0;
        }
        return new HashCodeBuilder(17, 37).append(getValueIfNotEmpty()).toHashCode();
    }
}
