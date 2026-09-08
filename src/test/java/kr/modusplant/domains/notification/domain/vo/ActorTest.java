package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_ACTOR_ID;
import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_ACTOR_NICKNAME;
import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    @Nested
    @DisplayName("Actor 생성 테스트")
    class CreationTests {

        @Test
        @DisplayName("유효한 UUID와 닉네임으로 Actor 반환")
        void testFromUuidWithNickname_givenValidParams_willReturnActor() {
            // when
            Actor actor = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_ACTOR_NICKNAME);

            // then
            assertNotNull(actor);
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, actor.getId());
            assertEquals(TEST_NOTIFICATION_ACTOR_NICKNAME, actor.getNickname());
        }

        @Test
        @DisplayName("유효한 UUID 문자열과 닉네임으로 Actor 반환")
        void testFromStringWithNickname_givenValidParams_willReturnActor() {
            // when
            Actor actor = Actor.fromStringWithNickname(TEST_NOTIFICATION_ACTOR_ID.toString(), TEST_NOTIFICATION_ACTOR_NICKNAME);

            // then
            assertNotNull(actor);
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, actor.getId());
        }

        @Test
        @DisplayName("닉네임이 null일 때 예외 반환")
        void testFromUuidWithNickname_givenNullNickname_willThrowException() {
            // given & when & then
            assertThrows(EmptyValueException.class, () ->
                    Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, null));
        }

        @Test
        @DisplayName("닉네임 패턴 위반 시 예외 반환")
        void testFromUuidWithNickname_givenInvalidNicknamePattern_willThrowException() {
            // given & when & then
            assertThrows(InvalidValueException.class, () ->
                    Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, "@@@잘못된닉네임@@@"));
        }

    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("ID와 닉네임이 같을 때 참 반환")
        void testEquals_givenSameIdAndNickname_willReturnTrue() {
            Actor actor1 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_ACTOR_NICKNAME);
            Actor actor2 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_ACTOR_NICKNAME);

            assertEquals(actor1, actor2);
            assertEquals(actor1.hashCode(), actor2.hashCode());
        }

        @Test
        @DisplayName("닉네임이 다를 때 거짓 반환")
        void testEquals_givenDifferentNickname_willReturnFalse() {
            Actor actor1 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, "테스터1");
            Actor actor2 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, "테스터2");

            assertNotEquals(actor1, actor2);
            assertNotEquals(actor1.hashCode(), actor2.hashCode());
        }

    }
}
