package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyNicknameException;
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
public class Nickname {
    private final String value;

    public static Nickname create(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyNicknameException();
        } else if (!PATTERN_NICKNAME.matcher(value).matches()) {
            throw new InvalidDataException(ErrorCode.INVALID_INPUT, "nickname");
        }
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
