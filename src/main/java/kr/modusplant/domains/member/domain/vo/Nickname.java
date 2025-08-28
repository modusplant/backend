package kr.modusplant.domains.member.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Nickname {
    private final String value;

    public static Nickname of(String value) {
        return new Nickname(value);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Nickname nickname)) return false;

        return new EqualsBuilder().append(getValue(), nickname.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
