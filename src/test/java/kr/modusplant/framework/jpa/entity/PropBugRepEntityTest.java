package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.PropBugRepEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PropBugRepEntityTest implements PropBugRepEntityTestUtils {
    private PropBugRepEntity testPropBugRepEntity;

    @BeforeEach
    public void beforeEach() {
        testPropBugRepEntity = createPropBugRepEntityBuilderWithUlid()
                .member(createMemberBasicUserEntityWithUuid())
                .build();
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testPropBugRepEntity.getETagSource(), TEST_REPORT_ULID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testPropBugRepEntity, testPropBugRepEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testPropBugRepEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testPropBugRepEntity, PropBugRepEntity.builder().propBugRep(testPropBugRepEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testPropBugRepEntity.hashCode(), testPropBugRepEntity.hashCode());
    }
}