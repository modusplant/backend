package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommPostBookmarkEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.CommPostBookmarkId;
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
public class CommPostBookmarkEntityTest implements CommPostBookmarkEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_COMM_POST_ULID;
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        CommPostBookmarkEntity commPostBookmarkEntity = CommPostBookmarkEntity.of(postId, memberId);
        entityManager.persistAndFlush(commPostBookmarkEntity);
    }

    @Test
    @DisplayName("게시글 북마크")
    void likeCommPost_success() {
        // when
        CommPostBookmarkEntity commPostBookmarkEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));

        // then
        assertThat(commPostBookmarkEntity).isNotNull();
        assertThat(commPostBookmarkEntity.getPostId()).isEqualTo(postId);
        assertThat(commPostBookmarkEntity.getMemberId()).isEqualTo(memberId);
        assertThat(commPostBookmarkEntity.getCreatedAt()).isNotNull();
        assertThat(commPostBookmarkEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("게시글 북마크 삭제")
    void unlikeCommPost_success() {
        // when
        CommPostBookmarkEntity commPostBookmarkEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));
        entityManager.remove(commPostBookmarkEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        CommPostBookmarkEntity deletedEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));
        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // when
        CommPostBookmarkEntity commPostBookmarkEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));

        // then
        //noinspection EqualsWithItself
        assertEquals(commPostBookmarkEntity, commPostBookmarkEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        // when
        CommPostBookmarkEntity commPostBookmarkEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));

        // then
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(commPostBookmarkEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        // when
        CommPostBookmarkEntity commPostBookmarkEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));

        // then
        assertEquals(CommPostBookmarkEntity.of(commPostBookmarkEntity.getPostId(), commPostBookmarkEntity.getMemberId()),
                CommPostBookmarkEntity.of(commPostBookmarkEntity.getPostId(), commPostBookmarkEntity.getMemberId()));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        // when
        CommPostBookmarkEntity commPostBookmarkEntity = entityManager.find(CommPostBookmarkEntity.class, new CommPostBookmarkId(postId, memberId));

        // then
        assertEquals(commPostBookmarkEntity.hashCode(), commPostBookmarkEntity.hashCode());
    }
}