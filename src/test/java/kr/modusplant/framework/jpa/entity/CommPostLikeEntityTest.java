package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommPostLikeEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.CommPostLikeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
public class CommPostLikeEntityTest implements CommPostLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_COMM_POST_ULID;
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        CommPostLikeEntity commPostLikeEntity = CommPostLikeEntity.of(postId, memberId);
        entityManager.persistAndFlush(commPostLikeEntity);
    }

    @Test
    @DisplayName("게시글 좋아요")
    void likeCommPost_success() {
        // when
        CommPostLikeEntity commPostLikeEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));

        // then
        assertThat(commPostLikeEntity).isNotNull();
        assertThat(commPostLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(commPostLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(commPostLikeEntity.getCreatedAt()).isNotNull();
        assertThat(commPostLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("게시글 좋아요 삭제")
    void unlikeCommPost_success() {
        // when
        CommPostLikeEntity commPostLikeEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));
        entityManager.remove(commPostLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        CommPostLikeEntity deletedEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));
        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // when
        CommPostLikeEntity commPostLikeEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));

        // then
        //noinspection EqualsWithItself
        assertEquals(commPostLikeEntity, commPostLikeEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // when
        CommPostLikeEntity commPostLikeEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commPostLikeEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        // when
        CommPostLikeEntity commPostLikeEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));

        // then
        assertEquals(CommPostLikeEntity.of(commPostLikeEntity.getPostId(), commPostLikeEntity.getMemberId()),
                CommPostLikeEntity.of(commPostLikeEntity.getPostId(), commPostLikeEntity.getMemberId()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // when
        CommPostLikeEntity commPostLikeEntity = entityManager.find(CommPostLikeEntity.class, new CommPostLikeId(postId, memberId));

        // then
        assertEquals(commPostLikeEntity.hashCode(), commPostLikeEntity.hashCode());
    }
}