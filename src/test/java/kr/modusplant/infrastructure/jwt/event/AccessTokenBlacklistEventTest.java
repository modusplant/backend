package kr.modusplant.infrastructure.jwt.event;

import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccessTokenBlacklistEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("유효한 접근 토큰으로 AccessTokenBlacklistEvent 반환")
        void testCreate_givenValidAccessToken_willReturnAccessTokenBlacklistEvent() {
            // given & when
            AccessTokenBlacklistEvent event = AccessTokenBlacklistEvent.create(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);

            // then
            assertThat(event.getAccessToken()).isEqualTo(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("accessToken이 null일 때 예외 반환")
        void testCreate_givenNullAccessToken_willThrowException() {
            // given & when
            EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, () ->
                    AccessTokenBlacklistEvent.create(null));

            // then
            assertThat(emptyValueException.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }
    }
}
