package kr.modusplant.domains.post.framework.outbound.jpa.entity;

import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostArchiveEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PostArchiveEntityTest implements PostArchiveEntityTestUtils {

    private PostArchiveEntity testPostArchiveEntity;

    @BeforeEach
    public void beforeEach() {
        testPostArchiveEntity = createPostArchiveEntity();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testPostArchiveEntity, testPostArchiveEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testMemberId, testPostArchiveEntity);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testPostArchiveEntity, PostArchiveEntity.builder().postArchive(testPostArchiveEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testPostArchiveEntity.hashCode(), testPostArchiveEntity.hashCode());
    }
}