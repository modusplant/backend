package kr.modusplant.shared.kernel.validator;

import kr.modusplant.infrastructure.swear.common.util.SwearHolderTestUtils;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.kernel.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NicknameValidatorTest {
    private final NicknameValidator nicknameValidator =
            new NicknameValidator(SwearHolderTestUtils.createSwearHolder());

    @ParameterizedTest
    @ValueSource(strings = {"시1발", "시이발", "tlqkf", "바보"})
    @DisplayName("비속어를 포함한 닉네임으로 예외 반환")
    void testValidate_givenNicknameWithSwear_willThrowException(String value) {
        // given
        Nickname nickname = Nickname.create(value);

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> nicknameValidator.validate(nickname));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.SWEAR_CONTAINED_NICKNAME);
    }

    @Test
    @DisplayName("비속어를 포함하지 않은 닉네임으로 검증 수행")
    void testValidate_givenNicknameWithoutSwear_willValidateNickname() {
        // given
        Nickname nickname = Nickname.create(MEMBER_BASIC_USER_NICKNAME);

        // when & then
        assertDoesNotThrow(() -> nicknameValidator.validate(nickname));
    }
}
