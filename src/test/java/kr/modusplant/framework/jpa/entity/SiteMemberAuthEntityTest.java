package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.SiteMemberAuthEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SiteMemberAuthEntityTest implements SiteMemberAuthEntityTestUtils {
    private SiteMemberAuthEntity testSiteMemberAuthEntity;

    @BeforeEach
    public void beforeEach() {
        testSiteMemberAuthEntity = createMemberAuthBasicUserEntityBuilder()
                .member(createMemberBasicUserEntity())
                .build();
        ReflectionTestUtils.setField(testSiteMemberAuthEntity, "uuid", MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testSiteMemberAuthEntity.getETagSource(),MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSiteMemberAuthEntity, testSiteMemberAuthEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSiteMemberAuthEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testSiteMemberAuthEntity, SiteMemberAuthEntity.builder().memberAuth(testSiteMemberAuthEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSiteMemberAuthEntity.hashCode(), testSiteMemberAuthEntity.hashCode());
    }
}