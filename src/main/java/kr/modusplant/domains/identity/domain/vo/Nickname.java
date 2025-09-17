package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.constant.IdentityDataFormat;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Nickname {
    private final String nickname;

    public static Nickname create(String input) {
        Nickname.validateSource(input);
        return new Nickname(input);
    }

    public static void validateSource(String input) {
        if (input.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME); }
        if (!input.matches(IdentityDataFormat.NICKNAME_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_NICKNAME);
        }
    }
}
