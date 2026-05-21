package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileImageBytes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileImageBytes {
    private final byte[] value;

    public static MemberProfileImageBytes create(byte[] value) {
        if (value == null) {
            return EmptyMemberProfileImageBytes.create();
        }
        return new MemberProfileImageBytes(value);
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
