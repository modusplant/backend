package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SiteMemberTermEntityTest implements SiteMemberTermEntityTestUtils {
    private SiteMemberTermEntity testSiteMemberTermEntity;

    @BeforeEach
    public void beforeEach() {
        testSiteMemberTermEntity = createMemberTermUserEntityWithUuid();
        ReflectionTestUtils.setField(testSiteMemberTermEntity, "uuid", MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testSiteMemberTermEntity.getETagSource(),MEMBER_BASIC_USER_UUID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSiteMemberTermEntity, testSiteMemberTermEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSiteMemberTermEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testSiteMemberTermEntity, SiteMemberTermEntity.builder().memberTerm(testSiteMemberTermEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSiteMemberTermEntity.hashCode(), testSiteMemberTermEntity.hashCode());
    }
}