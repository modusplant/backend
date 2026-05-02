package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.generator.RandomUlidGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchPostIdTest {
    private final RandomUlidGenerator randomUlidGenerator = new UlidIdGenerator();

    @Test
    @DisplayName("create으로 보고서 제목 반환")
    void testCreate_givenValidValue_willReturnSearchPostId() {
        assertThat(SearchPostId.create(TEST_POST_ULID)).isEqualTo(SearchPostId.create(TEST_POST_ULID));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> SearchPostId.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_POST_ID);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> SearchPostId.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_POST_ID);
    }

    @Test
    @DisplayName("패턴에 매칭되지 않는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenMismatchedString_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> SearchPostId.create("a".repeat(10)));
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.INVALID_SEARCH_POST_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchPostId, testSearchPostId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchPostId, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSearchPostId, SearchPostId.create(randomUlidGenerator.generate()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchPostId.hashCode(), testSearchPostId.hashCode());
    }
}