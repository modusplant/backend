package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PostLikeCountTest implements PostTestUtils {

    @Nested
    @DisplayName("LikeCount 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("zero()로 LikeCount 반환")
        void testZero_givenNothing_willReturnLikeCount() {
            // when
            PostLikeCount postLikeCount = PostLikeCount.zero();

            // then
            assertNotNull(postLikeCount);
            assertEquals(0, postLikeCount.getValue());
        }

        @Test
        @DisplayName("유효한 정수 값으로 LikeCount 반환")
        void testCreate_givenValidParameter_willReturnLikeCount() {
            // given
            int largeValue = Integer.MAX_VALUE;

            // when
            PostLikeCount postLikeCount1 = PostLikeCount.create(0);
            PostLikeCount postLikeCount2 = PostLikeCount.create(largeValue);

            // then
            assertNotNull(testPostLikeCount);
            assertEquals(15, testPostLikeCount.getValue());
            assertNotNull(postLikeCount1);
            assertEquals(0, postLikeCount1.getValue());
            assertNotNull(postLikeCount2);
            assertEquals(largeValue, postLikeCount2.getValue());
        }

        @Test
        @DisplayName("음수 값일 때 예외 반환")
        void testCreate_givenMinusValue_willThrowException() {
            // when & then
            assertThrows(InvalidValueException.class, () -> PostLikeCount.create(-1));
            assertThrows(InvalidValueException.class, () -> PostLikeCount.create(Integer.MIN_VALUE));
        }

    }

    @Nested
    @DisplayName("LikeCount 증가 테스트")
    class IncrementTests {

        @Test
        @DisplayName("increment 시 LikeCount 반환")
        void testIncrement_givenLikeCount_willReturnLikeCount() {
            // given
            PostLikeCount postLikeCount1 = PostLikeCount.zero();
            PostLikeCount postLikeCount2 = PostLikeCount.create(1000000);

            // when
            PostLikeCount incrementedCount1 = postLikeCount1.increment();
            PostLikeCount incrementedCount2 = postLikeCount2.increment();
            PostLikeCount incrementedCount3 = testPostLikeCount.increment();


            // then
            assertEquals(postLikeCount1.getValue()+1, incrementedCount1.getValue());
            assertEquals(postLikeCount2.getValue()+1,incrementedCount2.getValue());
            assertEquals(16, incrementedCount3.getValue());
        }
    }

    @Nested
    @DisplayName("LikeCount 감소 테스트")
    class DecrementTests {

        @Test
        @DisplayName("1 이상에서 decrement 시 LikeCount 반환")
        void testDecrement_givenPositiveLikeCount_willReturnLikeCount() {
            // given
            PostLikeCount postLikeCount1 = PostLikeCount.create(1);
            PostLikeCount postLikeCount2 = PostLikeCount.create(1000000);

            // when
            PostLikeCount decrementedCount1 = postLikeCount1.decrement();
            PostLikeCount decrementedCount2 = postLikeCount2.decrement();
            PostLikeCount decrementedCount3 = testPostLikeCount.decrement();

            // then
            assertEquals(postLikeCount1.getValue()-1, decrementedCount1.getValue());
            assertEquals(postLikeCount2.getValue()-1, decrementedCount2.getValue());
            assertEquals(14, decrementedCount3.getValue());
        }

        @Test
        @DisplayName("0에서 decrement 시 LikeCount 반환")
        void testDecrement_givenZeroLikeCount_willReturnLikeCount() {
            // given
            PostLikeCount postLikeCount = PostLikeCount.zero();

            // when
            PostLikeCount decrementedCount = postLikeCount.decrement();

            // then
            assertEquals(0, decrementedCount.getValue());
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(testPostLikeCount, testPostLikeCount);
            assertEquals(testPostLikeCount.hashCode(), testPostLikeCount.hashCode());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // when & then
            assertNotEquals(testPostLikeCount, testPostId);
            assertNotEquals(testPostLikeCount.hashCode(), testPostId.hashCode());
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // when & then
            assertNotEquals(testPostLikeCount, PostLikeCount.zero());
        }

    }

}