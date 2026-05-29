package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.ProposalBugReportEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ProposalOrBugReportEntityTest implements ProposalBugReportEntityTestUtils {
    private ProposalOrBugReportEntity testProposalOrBugReportEntity;

    @BeforeEach
    public void beforeEach() {
        testProposalOrBugReportEntity = createProposalBugReportEntityBuilder()
                .member(createMemberBasicUserEntityWithUuid())
                .build();
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testProposalOrBugReportEntity.getETagSource(), TEST_REPORT_ULID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testProposalOrBugReportEntity, testProposalOrBugReportEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testProposalOrBugReportEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testProposalOrBugReportEntity, ProposalOrBugReportEntity.builder().proposalBugReport(testProposalOrBugReportEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testProposalOrBugReportEntity.hashCode(), testProposalOrBugReportEntity.hashCode());
    }
}