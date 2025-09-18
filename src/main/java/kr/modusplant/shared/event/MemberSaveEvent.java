package kr.modusplant.shared.event;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.constant.IdentityDataFormat;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.domains.identity.domain.vo.Credentials;
import kr.modusplant.domains.identity.domain.vo.Nickname;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSaveEvent {
    private final String email;
    private final String password;
    private final String nickname;

    public static MemberSaveEvent create(String email, String password, String nickname) {
        validateEmail(email);
        validatePassword(password);
        validateNickname(nickname);
        return new MemberSaveEvent(email, password, nickname);
    }

    private static void validateEmail(String email) {
        if (email.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_EMAIL); }
        if (!email.matches(IdentityDataFormat.EMAIL_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_EMAIL);
        }
    }

    public static void validatePassword(String password) {
        if (password.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_PASSWORD); }
        if (!password.matches(IdentityDataFormat.PASSWORD_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_PASSWORD);
        }
    }

    public static void validateNickname(String input) {
        if (input.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME); }
        if (!input.matches(IdentityDataFormat.NICKNAME_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_NICKNAME);
        }
    }
}
