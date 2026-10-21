package kr.modusplant.domains.comment.domain.vo.nullobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils.testCommentPath;
import static kr.modusplant.domains.comment.common.util.domain.vo.ReplyToCommentPathTestUtils.testReplyToCommentPath;
import static kr.modusplant.domains.comment.common.util.domain.nullobject.EmptyReplyToCommentPathTestUtils.testEmptyReplyToCommentPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmptyReplyToCommentPathTest {
    @Test
    @DisplayName("create로 비어 있는 답글 대상 댓글 경로 반환")
    void testCreate_givenNothing_willReturnEmptyReplyToCommentPath() {
        assertThat(EmptyReplyToCommentPath.create()).isEqualTo(EmptyReplyToCommentPath.create());
    }

    @Test
    @DisplayName("getValue로 null 반환")
    void testGetValue_givenNothing_willReturnNull() {
        assertThat(testEmptyReplyToCommentPath.getValue()).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testEmptyReplyToCommentPath, testEmptyReplyToCommentPath);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testEmptyReplyToCommentPath, testCommentPath);
    }

    @Test
    @DisplayName("ReplyToCommentPath 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testEmptyReplyToCommentPath, testReplyToCommentPath);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testEmptyReplyToCommentPath.hashCode(), testEmptyReplyToCommentPath.hashCode());
    }
}
