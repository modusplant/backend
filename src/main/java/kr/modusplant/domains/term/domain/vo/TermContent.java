package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptyTermContentException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TermContent {
    private final String value;

    public static TermContent create(String value) {
        if (value == null || value.isBlank()) {
            throw new EmptyTermContentException();
        }
        return new TermContent(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermContent termContent)) return false;
        return new EqualsBuilder().append(getValue(), termContent.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
