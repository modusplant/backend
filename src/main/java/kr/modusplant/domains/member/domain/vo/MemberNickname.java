package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.shared.constant.Regex.PATTERN_NICKNAME;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberNickname {
    private final String value;

    public static MemberNickname create(String value) {
        if (value == null) {
            throw new EmptyMemberNicknameException();
        } else if (!PATTERN_NICKNAME.matcher(value).matches()) {
            throw new InvalidDataException(ErrorCode.INVALID_INPUT, "nickname");
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
