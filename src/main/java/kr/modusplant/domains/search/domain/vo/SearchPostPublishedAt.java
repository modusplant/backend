package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.domains.search.domain.vo.nullobject.EmptySearchPostPublishedAt;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.SEARCH_POST_PUBLISHED_AT_AFTER_NOW;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPostPublishedAt {
    private final LocalDateTime value;

    public static SearchPostPublishedAt create(LocalDateTime value) {
        if (value == null) {
            return EmptySearchPostPublishedAt.create();
        }
        if (value.isAfter(LocalDateTime.now())) {
            throw new InvalidValueException(SEARCH_POST_PUBLISHED_AT_AFTER_NOW, "searchPostPublishedAt");
        }
        return new SearchPostPublishedAt(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPostPublishedAt searchPostPublishedAt)) return false;

        return new EqualsBuilder().append(getValue(), searchPostPublishedAt.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
