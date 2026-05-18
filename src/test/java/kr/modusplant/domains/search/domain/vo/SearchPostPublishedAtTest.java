package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostPublishedAtTestUtils.testEmptySearchPostPublishedAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchPostPublishedAtTest {
    @Test
    @DisplayName("create으로 보고서 제목 반환")
    void testCreate_givenValidValue_willReturnSearchPostPublishedAt() {
        assertThat(SearchPostPublishedAt.create(TEST_POST_PUBLISHED_AT)).isEqualTo(SearchPostPublishedAt.create(TEST_POST_PUBLISHED_AT));
    }

    @Test
    @DisplayName("null로 create을 호출하여 널 객체 반환")
    void testCreate_givenNull_willReturnEmptySearchPostPublishedAt() {
        assertThat(SearchPostPublishedAt.create(null)).isEqualTo(testEmptySearchPostPublishedAt);
    }

    @Test
    @DisplayName("미래의 값으로 create을 호출하여 오류 발생")
    void testCreate_givenFutureValue_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> SearchPostPublishedAt.create(LocalDateTime.now().plusDays(1)));
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.SEARCH_POST_PUBLISHED_AT_AFTER_NOW);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchPostPublishedAt, testSearchPostPublishedAt);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchPostPublishedAt, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSearchPostPublishedAt, SearchPostPublishedAt.create(TEST_POST_PUBLISHED_AT.minusDays(1)));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchPostPublishedAt.hashCode(), testSearchPostPublishedAt.hashCode());
    }
}