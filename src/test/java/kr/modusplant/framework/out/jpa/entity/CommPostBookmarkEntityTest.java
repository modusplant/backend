package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.framework.out.jpa.entity.common.util.CommPostBookmarkEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.CommPostBookmarkId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;

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
}