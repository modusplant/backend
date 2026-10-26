package kr.modusplant.domains.comment.domain.vo.nullobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.comment.common.util.domain.vo.nullobject.EmptyCommentAuthorIdTestUtils.testEmptyCommentAuthorId;
import static kr.modusplant.domains.comment.common.util.domain.vo.CommentAuthorIdTestUtils.testCommentAuthorId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyCommentAuthorIdTest {
    @Test
    @DisplayName("create로 비어 있는 작성자 id 반환")
    void testCreate_givenNothing_willReturnEmptyCommentAuthorId() {
        assertThat(EmptyCommentAuthorId.create()).isSameAs(EmptyCommentAuthorId.create());
    }

    @Test
    @DisplayName("getValue로 null 반환")
    void testGetValue_givenNothing_willReturnNull() {
        assertThat(testEmptyCommentAuthorId.getValue()).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testEmptyCommentAuthorId, testEmptyCommentAuthorId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testEmptyCommentAuthorId, UUID.randomUUID());
    }

    @Test
    @DisplayName("CommentAuthorId 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testEmptyCommentAuthorId, testCommentAuthorId);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testEmptyCommentAuthorId.hashCode(), testEmptyCommentAuthorId.hashCode());
    }
}
