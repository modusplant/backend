package kr.modusplant.shared.kernel;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidNicknameException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
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
        if (value == null || value.isBlank()) {
            throw new EmptyValueException(KernelErrorCode.EMPTY_NICKNAME, "nickname");
        } else if (!PATTERN_NICKNAME.matcher(value).matches()) {
            throw new InvalidNicknameException();
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
