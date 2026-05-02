package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.EMPTY_SEARCH_KEYWORD;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchKeyword {
    private final String value;

    public static SearchKeyword create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_SEARCH_KEYWORD, "searchKeyword");
        }
        return new SearchKeyword(value.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchKeyword searchKeyword)) return false;

        return new EqualsBuilder().append(getValue(), searchKeyword.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
