package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyMemberProfileImagePath extends MemberProfileImagePath {

    public static EmptyMemberProfileImagePath create() {
        return new EmptyMemberProfileImagePath();
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileImagePath memberProfileImagePath)) return false;

        return new EqualsBuilder().append(getValue(), memberProfileImagePath.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
