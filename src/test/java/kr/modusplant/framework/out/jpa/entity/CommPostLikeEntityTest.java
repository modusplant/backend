package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.framework.out.jpa.entity.common.util.CommPostLikeEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.CommPostLikeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.framework.out.jpa.entity.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("소통 게시글 좋아요")
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
    @DisplayName("소통 게시글 좋아요 삭제")
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
}