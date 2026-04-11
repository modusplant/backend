package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportCategoryTestUtils.testReportCategoryBugReport;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportCategoryTestUtils.testReportCategoryProposal;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_CATEGORY_BUG_REPORT;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_CATEGORY_PROPOSAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportCategoryTest {
    @Test
    @DisplayName("create으로 proposal에 대한 보고서 항목 반환")
    void testCreate_givenProposal_willReturnReportCategory() {
        assertThat(ReportCategory.create(TEST_REPORT_CATEGORY_PROPOSAL)).isEqualTo(ReportCategory.create(TEST_REPORT_CATEGORY_PROPOSAL));
    }

    @Test
    @DisplayName("create으로 bugReport에 대한 보고서 항목 반환")
    void testCreate_givenBugReport_willReturnReportCategory() {
        assertThat(ReportCategory.create(TEST_REPORT_CATEGORY_BUG_REPORT)).isEqualTo(ReportCategory.create(TEST_REPORT_CATEGORY_BUG_REPORT));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportCategory.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_CONTENT);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportCategory.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_CONTENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "values"})
    @DisplayName("proposal 또는 bugReport가 아닌 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringNotEqualToProposalOrBugReport_willThrowException(String value) {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ReportCategory.create(value));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_REPORT_CATEGORY);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReportCategoryProposal, testReportCategoryProposal);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReportCategoryProposal, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testReportCategoryProposal, testReportCategoryBugReport);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testReportCategoryProposal.hashCode(), testReportCategoryProposal.hashCode());
    }
}