package kr.modusplant.domains.member.domain.vo.nullobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.testReportImagePath;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyReportImagePathTestUtils.testEmptyReportImagePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyReportImagePathTest {
    @Test
    @DisplayName("create로 비어 있는 보고서 이미지 경로 반환")
    void testCreate_givenNothing_willReturnEmptyReportImagePath() {
        assertThat(EmptyReportImagePath.create()).isEqualTo(EmptyReportImagePath.create());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testEmptyReportImagePath, testEmptyReportImagePath);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testEmptyReportImagePath, testMemberId);
    }

    @Test
    @DisplayName("ReportImagePath 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testEmptyReportImagePath, testReportImagePath);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testEmptyReportImagePath.hashCode(), testEmptyReportImagePath.hashCode());
    }
}