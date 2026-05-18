package kr.modusplant.domains.member.framework.out.jpa.entity;

import kr.modusplant.domains.member.framework.out.jpa.compositekey.PostLikeCompositeKey;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.PostLikeEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
public class PostLikeEntityTest implements PostLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_POST_ULID;
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        PostLikeEntity postLikeEntity = PostLikeEntity.of(postId, memberId);
        entityManager.persistAndFlush(postLikeEntity);
    }

    @Test
    @DisplayName("게시글 좋아요")
    void likeCommPost_success() {
        // when
        PostLikeEntity postLikeEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));

        // then
        assertThat(postLikeEntity).isNotNull();
        assertThat(postLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(postLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(postLikeEntity.getCreatedAt()).isNotNull();
        assertThat(postLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("게시글 좋아요 삭제")
    void unlikeCommPost_success() {
        // when
        PostLikeEntity postLikeEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));
        entityManager.remove(postLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        PostLikeEntity deletedEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));
        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // when
        PostLikeEntity postLikeEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));

        // then
        //noinspection EqualsWithItself
        assertEquals(postLikeEntity, postLikeEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // when
        PostLikeEntity postLikeEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(postLikeEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        // when
        PostLikeEntity postLikeEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));

        // then
        assertEquals(PostLikeEntity.of(postLikeEntity.getPostId(), postLikeEntity.getMemberId()),
                PostLikeEntity.of(postLikeEntity.getPostId(), postLikeEntity.getMemberId()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // when
        PostLikeEntity postLikeEntity = entityManager.find(PostLikeEntity.class, new PostLikeCompositeKey(postId, memberId));

        // then
        assertEquals(postLikeEntity.hashCode(), postLikeEntity.hashCode());
    }
}