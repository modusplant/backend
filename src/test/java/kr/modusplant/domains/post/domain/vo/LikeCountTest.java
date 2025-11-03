package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.InvalidLikeCountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LikeCountTest implements PostTestUtils {

    @Nested
    @DisplayName("LikeCount 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("zero() 메서드로 0 값의 LikeCount를 생성한다")
        void testZero_givenNothing_willReturnZero() {
            // when
            LikeCount likeCount = LikeCount.zero();

            // then
            assertNotNull(likeCount);
            assertEquals(0, likeCount.getValue());
        }

        @Test
        @DisplayName("유효한 정수 값으로 LikeCount를 생성한다")
        void testCreate_givenValidParameter_willReturnLikeCount() {
            // given
            int largeValue = Integer.MAX_VALUE;

            // when
            LikeCount likeCount1 = LikeCount.create(0);
            LikeCount likeCount2 = LikeCount.create(largeValue);

            // then
            assertNotNull(testLikeCount);
            assertEquals(15, testLikeCount.getValue());
            assertNotNull(likeCount1);
            assertEquals(0, likeCount1.getValue());
            assertNotNull(likeCount2);
            assertEquals(largeValue, likeCount2.getValue());
        }

        @Test
        @DisplayName("음수 값으로 LikeCount 생성 시 InvalidLikeCountException을 발생시킨다")
        void testCreate_givenMinusValue_willThrowException() {
            // when & then
            assertThrows(InvalidLikeCountException.class, () -> LikeCount.create(-1));
            assertThrows(InvalidLikeCountException.class, () -> LikeCount.create(Integer.MIN_VALUE));
        }

    }

    @Nested
    @DisplayName("LikeCount 증가 테스트")
    class IncrementTests {

        @Test
        @DisplayName("increment 시 좋아요수+1을 반환한다")
        void testIncrement_givenLikeCount_willReturnIncrementedLikeCount() {
            // given
            LikeCount likeCount1 = LikeCount.zero();
            LikeCount likeCount2 = LikeCount.create(1000000);

            // when
            LikeCount incrementedCount1 = likeCount1.increment();
            LikeCount incrementedCount2 = likeCount2.increment();
            LikeCount incrementedCount3 = testLikeCount.increment();


            // then
            assertEquals(likeCount1.getValue()+1, incrementedCount1.getValue());
            assertEquals(likeCount2.getValue()+1,incrementedCount2.getValue());
            assertEquals(16, incrementedCount3.getValue());
        }
    }

    @Nested
    @DisplayName("LikeCount 감소 테스트")
    class DecrementTests {

        @Test
        @DisplayName("1 이상의 좋아요수에서 decrement 시 좋아요수-1을 반환한다")
        void testDecrement_givenLikeCount_willReturnLikeCountMinusOne() {
            // given
            LikeCount likeCount1 = LikeCount.create(1);
            LikeCount likeCount2 = LikeCount.create(1000000);

            // when
            LikeCount decrementedCount1 = likeCount1.decrement();
            LikeCount decrementedCount2 = likeCount2.decrement();
            LikeCount decrementedCount3 = testLikeCount.decrement();

            // then
            assertEquals(likeCount1.getValue()-1, decrementedCount1.getValue());
            assertEquals(likeCount2.getValue()-1, decrementedCount2.getValue());
            assertEquals(14, decrementedCount3.getValue());
        }

        @Test
        @DisplayName("0에서 decrement 시 0으로 유지된다")
        void testDecrement_givenLikeCountIsZero_willReturnZero() {
            // given
            LikeCount likeCount = LikeCount.zero();

            // when
            LikeCount decrementedCount = likeCount.decrement();

            // then
            assertEquals(0, decrementedCount.getValue());
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testLikeCount, testLikeCount);
            assertEquals(testLikeCount.hashCode(), testLikeCount.hashCode());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testLikeCount, testPostId);
            assertNotEquals(testLikeCount.hashCode(), testPostId.hashCode());
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testLikeCount, LikeCount.zero());
        }

    }

}