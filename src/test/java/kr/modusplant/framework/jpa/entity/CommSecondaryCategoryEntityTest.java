package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommSecondaryCategoryEntityTest implements CommSecondaryCategoryEntityTestUtils {
    private CommSecondaryCategoryEntity testCommSecondaryCategoryEntity;

    @BeforeEach
    public void beforeEach() {
        testCommSecondaryCategoryEntity = createCommSecondaryCategoryEntityBuilderWithId()
                .primaryCategory(createCommPrimaryCategoryEntityWithId())
                .build();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testCommSecondaryCategoryEntity, testCommSecondaryCategoryEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testCommSecondaryCategoryEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testCommSecondaryCategoryEntity, CommSecondaryCategoryEntity.builder().commSecondaryCategory(testCommSecondaryCategoryEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testCommSecondaryCategoryEntity.hashCode(), testCommSecondaryCategoryEntity.hashCode());
    }
}