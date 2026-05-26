package kr.modusplant.domains.post.framework.out.jpa.entity;

import kr.modusplant.domains.post.framework.out.jpa.entity.common.util.PrimaryCategoryEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PrimaryCategoryEntityTest implements PrimaryCategoryEntityTestUtils {
    private PrimaryCategoryEntity testPrimaryCategoryEntity;

    @BeforeEach
    public void beforeEach() {
        testPrimaryCategoryEntity = createPrimaryCategoryEntityWithId();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testPrimaryCategoryEntity, testPrimaryCategoryEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testPrimaryCategoryEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testPrimaryCategoryEntity, PrimaryCategoryEntity.builder().primaryCategory(testPrimaryCategoryEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testPrimaryCategoryEntity.hashCode(), testPrimaryCategoryEntity.hashCode());
    }
}