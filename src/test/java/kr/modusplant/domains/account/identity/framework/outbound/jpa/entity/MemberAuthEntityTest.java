package kr.modusplant.domains.account.identity.framework.outbound.jpa.entity;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.common.util.MemberAuthEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MemberAuthEntityTest implements MemberAuthEntityTestUtils {
    private MemberAuthEntity testMemberAuthEntity;

    @BeforeEach
    public void beforeEach() {
        testMemberAuthEntity = createMemberAuthBasicUserEntityBuilder()
                .member(createMemberBasicUserEntity())
                .build();
        ReflectionTestUtils.setField(testMemberAuthEntity, "uuid", MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testMemberAuthEntity.getETagSource(), MEMBER_BASIC_USER_UUID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberAuthEntity, testMemberAuthEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberAuthEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testMemberAuthEntity, MemberAuthEntity.builder().memberAuth(testMemberAuthEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testMemberAuthEntity.hashCode(), testMemberAuthEntity.hashCode());
    }
}