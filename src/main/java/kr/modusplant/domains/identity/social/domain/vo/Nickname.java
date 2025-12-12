package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.domain.exception.EmptyNicknameException;
import kr.modusplant.domains.identity.social.domain.exception.InvalidNicknameException;
import kr.modusplant.shared.constant.Regex;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Nickname {
    private final String nickname;

    public static Nickname create(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new EmptyNicknameException();
        }
        if (!nickname.matches(Regex.REGEX_NICKNAME)) {
            throw new InvalidNicknameException();
        }
        return new Nickname(nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Nickname)) return false;

        return new EqualsBuilder().append(getNickname(), ((Nickname) o).getNickname()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getNickname()).toHashCode();
    }
}
