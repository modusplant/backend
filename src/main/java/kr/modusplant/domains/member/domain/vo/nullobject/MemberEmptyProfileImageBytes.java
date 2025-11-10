package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberEmptyProfileImageBytes extends MemberProfileImageBytes {

    public static MemberEmptyProfileImageBytes create() {
        return new MemberEmptyProfileImageBytes();
    }

    @Override
    public byte[] getValue() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileImageBytes memberProfileImageBytes)) return false;

        return new EqualsBuilder().append(getValue(), memberProfileImageBytes.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
