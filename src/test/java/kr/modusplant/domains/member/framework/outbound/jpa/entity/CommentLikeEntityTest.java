package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.CommentLikeEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.CommentLikeCompositeKey;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
public class CommentLikeEntityTest implements CommentLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;
    private String path;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_POST_ULID;
        path = TEST_COMM_COMMENT_PATH;
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        CommentLikeEntity commentLikeEntity = CommentLikeEntity.of(postId, path, memberId);
        entityManager.persistAndFlush(commentLikeEntity);
    }

    @Test
    @DisplayName("댓글 좋아요")
    void likeCommComment_success() {
        // when
        CommentLikeEntity commentLikeEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));

        // then
        assertThat(commentLikeEntity).isNotNull();
        assertThat(commentLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(commentLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(commentLikeEntity.getCreatedAt()).isNotNull();
        assertThat(commentLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("댓글 좋아요 삭제")
    void unlikeCommComment_success() {
        // when
        CommentLikeEntity commentLikeEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));
        entityManager.remove(commentLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        CommentLikeEntity deletedEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));
        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // given & when
        CommentLikeEntity commentLikeEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));

        // then
        //noinspection EqualsWithItself
        assertEquals(commentLikeEntity, commentLikeEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // given & when
        CommentLikeEntity commentLikeEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commentLikeEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        CommentLikeEntity commentLikeEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));

        // then
        assertEquals(CommentLikeEntity.of(commentLikeEntity.getPostId(), commentLikeEntity.getPath(), commentLikeEntity.getMemberId()),
                CommentLikeEntity.of(commentLikeEntity.getPostId(), commentLikeEntity.getPath(), commentLikeEntity.getMemberId()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // given & when
        CommentLikeEntity commentLikeEntity = entityManager.find(CommentLikeEntity.class, new CommentLikeCompositeKey(postId, path, memberId));

        // then
        assertEquals(commentLikeEntity.hashCode(), commentLikeEntity.hashCode());
    }
}