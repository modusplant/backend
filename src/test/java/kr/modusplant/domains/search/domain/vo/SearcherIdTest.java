package kr.modusplant.domains.search.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordTestUtils.testSearchKeyword;
import static kr.modusplant.domains.search.common.util.domain.vo.SearcherIdTestUtils.testSearcherId;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearcherIdTestUtils.testEmptySearcherId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearcherIdTest {
    @Test
    @DisplayName("fromUuid로 검색자 ID 반환")
    void testFromUuid_givenValidValue_willReturnSearcherId() {
        assertThat(SearcherId.fromUuid(MEMBER_BASIC_USER_UUID)).isEqualTo(testSearcherId);
    }

    @Test
    @DisplayName("null로 fromUuid를 호출하여 널 객체 반환")
    void testFromUuid_givenNull_willReturnEmptySearcherId() {
        assertThat(SearcherId.fromUuid(null)).isEqualTo(testEmptySearcherId);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearcherId, testSearcherId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearcherId, testSearchKeyword);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSearcherId, SearcherId.fromUuid(UUID.randomUUID()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearcherId.hashCode(), testSearcherId.hashCode());
    }
}
