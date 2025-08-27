package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberIdException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberNickname {
    private final String value;

    public static MemberNickname of(String value) {
        if (value == null) {
            throw new EmptyMemberNicknameException();
        }
        return new MemberNickname(value);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberNickname memberNickname)) return false;

        return new EqualsBuilder().append(getValue(), memberNickname.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
