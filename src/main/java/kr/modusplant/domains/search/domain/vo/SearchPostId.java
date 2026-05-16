package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.domains.search.domain.vo.nullobject.EmptySearchPostId;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.INVALID_SEARCH_POST_ID;
import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPostId {
    private final String value;

    public static SearchPostId create(String value) {
        if (StringUtils.isBlank(value)) {
            return EmptySearchPostId.create();
        }
        if (!PATTERN_ULID.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_SEARCH_POST_ID, "searchPostId");
        }
        return new SearchPostId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPostId searchPostId)) return false;

        return new EqualsBuilder().append(getValue(), searchPostId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
