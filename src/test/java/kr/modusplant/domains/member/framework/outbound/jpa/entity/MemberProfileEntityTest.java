package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberProfileEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MemberProfileEntityTest implements MemberProfileEntityTestUtils {
    private MemberProfileEntity testMemberProfileEntity;

    @BeforeEach
    public void beforeEach() {
        testMemberProfileEntity = createMemberProfileBasicUserEntityBuilder()
                .member(createMemberBasicUserEntityWithUuid())
                .build();
        ReflectionTestUtils.setField(testMemberProfileEntity, "uuid", MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testMemberProfileEntity.getETagSource(),MEMBER_BASIC_USER_UUID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testMemberProfileEntity, testMemberProfileEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberProfileEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testMemberProfileEntity, MemberProfileEntity.builder().memberProfile(testMemberProfileEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testMemberProfileEntity.hashCode(), testMemberProfileEntity.hashCode());
    }
}