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
public class SearchKeywordSimilarity {
    private static final SearchKeywordSimilarity emptySearchKeywordSimilarity =
            new SearchKeywordSimilarity(0, true);

    private final double value;

    @Getter
    private final boolean empty;

    public static SearchKeywordSimilarity create(Double value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_SEARCH_KEYWORD_SIMILARITY, "searchKeywordSimilarity");
        } else if (value < 0 || value > 1) {
            throw new InvalidValueException(SEARCH_KEYWORD_SIMILARITY_OUT_OF_RANGE, "searchKeywordSimilarity");
        }
        return new SearchKeywordSimilarity(value, false);
    }

    public static SearchKeywordSimilarity createEmpty() {
        return emptySearchKeywordSimilarity;
    }

    public double getValueIfNotEmpty() {
        if (isEmpty()) {
            throw new InvalidValueException(NOT_FOUND_SEARCH_POST_IMPORTANCE, "searchPostImportance");
        }
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchKeywordSimilarity searchKeywordSimilarity)) return false;

        if (isEmpty() && searchKeywordSimilarity.isEmpty()) return true;

        if (isEmpty() || searchKeywordSimilarity.isEmpty()) return false;

        return new EqualsBuilder().append(getValueIfNotEmpty(), searchKeywordSimilarity.getValueIfNotEmpty()).isEquals();
    }

    @Override
    public int hashCode() {
        if (isEmpty()) {
            return 0;
        }
        return new HashCodeBuilder(17, 37).append(getValueIfNotEmpty()).toHashCode();
    }
}
