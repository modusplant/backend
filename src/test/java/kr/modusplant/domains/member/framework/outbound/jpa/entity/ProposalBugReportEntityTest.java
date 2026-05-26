package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.ProposalBugReportEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ProposalBugReportEntityTest implements ProposalBugReportEntityTestUtils {
    private ProposalBugReportEntity testProposalBugReportEntity;

    @BeforeEach
    public void beforeEach() {
        testProposalBugReportEntity = createProposalBugReportEntityBuilder()
                .member(createMemberBasicUserEntityWithUuid())
                .build();
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testProposalBugReportEntity.getETagSource(), TEST_REPORT_ULID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testProposalBugReportEntity, testProposalBugReportEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testProposalBugReportEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testProposalBugReportEntity, ProposalBugReportEntity.builder().proposalBugReport(testProposalBugReportEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testProposalBugReportEntity.hashCode(), testProposalBugReportEntity.hashCode());
    }
}