package kr.modusplant.domains.account.social.domain.event;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SocialAccessRevokeEventTest {
    @Nested
    @DisplayName("create 테스트")
    class CreateTest {
        @Test
        @DisplayName("유효한 파라미터로 SocialAccessRevokeEvent 반환")
        void testCreate_givenValidParameters_willReturnSocialAccessRevokeEvent() {
            // given & when
            SocialAccessRevokeEvent event = SocialAccessRevokeEvent.create(
                    MEMBER_BASIC_USER_UUID, SocialProvider.KAKAO.getValue(), TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);

            // then
            assertThat(event.getMemberId()).isEqualTo(MEMBER_BASIC_USER_UUID);
            assertThat(event.getAuthProvider()).isEqualTo(SocialProvider.KAKAO.getValue());
            assertThat(event.getSocialAccessToken()).isEqualTo(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("memberId가 null일 때 예외 반환")
        void testCreate_givenNullMemberId_willThrowException() {
            // given & when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class, () ->
                    SocialAccessRevokeEvent.create(null, SocialProvider.KAKAO.getValue(), TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
        }

        @Test
        @DisplayName("authProvider가 비어 있을 때 예외 반환")
        void testCreate_givenBlankAuthProvider_willThrowException() {
            // given & when
            EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, () ->
                    SocialAccessRevokeEvent.create(MEMBER_BASIC_USER_UUID, " ", TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN));

            // then
            assertThat(emptyValueException.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }

        @Test
        @DisplayName("socialAccessToken이 null일 때 예외 반환")
        void testCreate_givenNullSocialAccessToken_willThrowException() {
            // given & when
            EmptyValueException emptyValueException = assertThrows(EmptyValueException.class, () ->
                    SocialAccessRevokeEvent.create(MEMBER_BASIC_USER_UUID, SocialProvider.KAKAO.getValue(), null));

            // then
            assertThat(emptyValueException.getErrorCode()).isEqualTo(EMPTY_VALUE);
        }
    }
}
