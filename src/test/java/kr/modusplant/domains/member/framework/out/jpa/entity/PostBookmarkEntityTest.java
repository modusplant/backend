package kr.modusplant.domains.member.framework.out.jpa.entity;

import kr.modusplant.domains.member.framework.out.jpa.compositekey.PostBookmarkCompositeKey;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.PostBookmarkEntityTestUtils;
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
public class PostBookmarkEntityTest implements PostBookmarkEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_POST_ULID;
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        PostBookmarkEntity postBookmarkEntity = PostBookmarkEntity.of(postId, memberId);
        entityManager.persistAndFlush(postBookmarkEntity);
    }

    @Test
    @DisplayName("게시글 북마크")
    void likePost_success() {
        // when
        PostBookmarkEntity postBookmarkEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));

        // then
        assertThat(postBookmarkEntity).isNotNull();
        assertThat(postBookmarkEntity.getPostId()).isEqualTo(postId);
        assertThat(postBookmarkEntity.getMemberId()).isEqualTo(memberId);
        assertThat(postBookmarkEntity.getCreatedAt()).isNotNull();
        assertThat(postBookmarkEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("게시글 북마크 삭제")
    void unlikePost_success() {
        // when
        PostBookmarkEntity postBookmarkEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));
        entityManager.remove(postBookmarkEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        PostBookmarkEntity deletedEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));
        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // when
        PostBookmarkEntity postBookmarkEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));

        // then
        //noinspection EqualsWithItself
        assertEquals(postBookmarkEntity, postBookmarkEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // when
        PostBookmarkEntity postBookmarkEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(postBookmarkEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        // when
        PostBookmarkEntity postBookmarkEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));

        // then
        assertEquals(PostBookmarkEntity.of(postBookmarkEntity.getPostId(), postBookmarkEntity.getMemberId()),
                PostBookmarkEntity.of(postBookmarkEntity.getPostId(), postBookmarkEntity.getMemberId()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // when
        PostBookmarkEntity postBookmarkEntity = entityManager.find(PostBookmarkEntity.class, new PostBookmarkCompositeKey(postId, memberId));

        // then
        assertEquals(postBookmarkEntity.hashCode(), postBookmarkEntity.hashCode());
    }
}