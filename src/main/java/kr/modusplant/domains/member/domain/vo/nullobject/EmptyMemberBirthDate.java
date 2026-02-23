package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyMemberBirthDate extends MemberBirthDate {

    public static EmptyMemberBirthDate create() {
        return new EmptyMemberBirthDate();
    }

    @Override
    public LocalDate getValue() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberBirthDate MemberBirthDate)) return false;

        return new EqualsBuilder().append(getValue(), MemberBirthDate.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}