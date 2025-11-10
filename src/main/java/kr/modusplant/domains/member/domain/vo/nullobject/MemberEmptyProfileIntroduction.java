package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberEmptyProfileIntroduction extends MemberProfileIntroduction {

    public static MemberEmptyProfileIntroduction create() {
        return new MemberEmptyProfileIntroduction();
    }

    @Override
    public String getValue() {
        return null;
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
