package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ACTOR_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ACTOR_NICKNAME;
import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    @Nested
    @DisplayName("Actor 생성 테스트")
    class CreationTests {

        @Test
        @DisplayName("유효한 UUID와 닉네임으로 Actor를 생성한다")
        void testFromUuidWithNickname_givenValidParams_willReturnActor() {
            // when
            Actor actor = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_ACTOR_NICKNAME);

            // then
            assertNotNull(actor);
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, actor.getId());
            assertEquals(TEST_NOTIFICATION_ACTOR_NICKNAME, actor.getNickname());
        }

        @Test
        @DisplayName("유효한 UUID 문자열과 닉네임으로 Actor를 생성한다")
        void testFromStringWithNickname_givenValidParams_willReturnActor() {
            // when
            Actor actor = Actor.fromStringWithNickname(TEST_NOTIFICATION_ACTOR_ID.toString(), TEST_NOTIFICATION_ACTOR_NICKNAME);

            // then
            assertNotNull(actor);
            assertEquals(TEST_NOTIFICATION_ACTOR_ID, actor.getId());
        }

        @Test
        @DisplayName("닉네임이 null이거나 패턴에 맞지 않으면 예외를 발생시킨다")
        void testValidateNickname_givenInvalidNickname_willThrowException() {
            // null 체크
            assertThrows(EmptyValueException.class, () ->
                    Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, null));

            // 패턴 체크 (예: 특수문자 포함 등 PATTERN_NICKNAME 위반 시)
            assertThrows(InvalidValueException.class, () ->
                    Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, "@@@잘못된닉네임@@@"));
        }

    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("ID와 닉네임이 모두 같으면 equals는 true를 반환한다")
        void useEqual_givenSameIdAndNickname_willReturnTrue() {
            Actor actor1 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_ACTOR_NICKNAME);
            Actor actor2 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, TEST_NOTIFICATION_ACTOR_NICKNAME);

            assertEquals(actor1, actor2);
            assertEquals(actor1.hashCode(), actor2.hashCode());
        }

        @Test
        @DisplayName("ID가 같더라도 닉네임이 다르면 equals는 false를 반환한다")
        void useEqual_givenDifferentNickname_willReturnFalse() {
            Actor actor1 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, "테스터1");
            Actor actor2 = Actor.fromUuidWithNickname(TEST_NOTIFICATION_ACTOR_ID, "테스터2");

            assertNotEquals(actor1, actor2);
            assertNotEquals(actor1.hashCode(), actor2.hashCode());
        }

    }
}