package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.EMPTY_SEARCH_RESULT_LIST_SIZE;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.SEARCH_RESULT_LIST_SIZE_OUT_OF_RANGE;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchResultListSize {
    private final int value;

    public static SearchResultListSize create(Integer value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_SEARCH_RESULT_LIST_SIZE, "searchResultListSize");
        } else if (value <= 0 || value > 50) {
            throw new InvalidValueException(SEARCH_RESULT_LIST_SIZE_OUT_OF_RANGE, "searchResultListSize");
        }
        return new SearchResultListSize(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchResultListSize searchResultListSize)) return false;

        return new EqualsBuilder().append(getValue(), searchResultListSize.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
