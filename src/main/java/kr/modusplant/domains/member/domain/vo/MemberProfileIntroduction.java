package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileIntroductionException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileIntroduction {
    private final String value;

    public static MemberProfileIntroduction create(String value) {
        if (value == null) {
            throw new EmptyMemberProfileIntroductionException();
        }
        return new MemberProfileIntroduction(value);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileIntroduction memberProfileIntroduction)) return false;

        return new EqualsBuilder().append(getValue(), memberProfileIntroduction.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
