package kr.modusplant.domains.search.domain.vo.nullobject;

import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptySearchPostPublishedAt extends SearchPostPublishedAt {
    public static EmptySearchPostPublishedAt create() {
        return new EmptySearchPostPublishedAt();
    }

    @Override
    public LocalDateTime getValue() {
        return null;
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
