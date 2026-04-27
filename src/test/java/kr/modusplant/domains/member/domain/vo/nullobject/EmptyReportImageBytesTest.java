package kr.modusplant.domains.member.domain.vo.nullobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageBytesTestUtils.testReportImageBytes1;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyReportImageBytesTestUtils.testEmptyReportImageBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyReportImageBytesTest {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 이미지 바이트 반환")
    void testCreate_givenNothing_willReturnEmptyReportImageBytes() {
        assertThat(EmptyReportImageBytes.create()).isEqualTo(EmptyReportImageBytes.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testEmptyReportImageBytes, testEmptyReportImageBytes);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testEmptyReportImageBytes, testMemberId);
    }

    @Test
    @DisplayName("ReportImageBytes 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testEmptyReportImageBytes, testReportImageBytes1);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testEmptyReportImageBytes.hashCode(), testEmptyReportImageBytes.hashCode());
    }
}