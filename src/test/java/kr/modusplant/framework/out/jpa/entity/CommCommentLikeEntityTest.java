package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentLikeEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.persistence.compositekey.CommCommentLikeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class CommCommentLikeEntityTest implements CommCommentLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;
    private String path;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_COMM_POST_ULID;
        path = TEST_COMM_COMMENT_PATH;
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        CommCommentLikeEntity commCommentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        entityManager.persistAndFlush(commCommentLikeEntity);
    }

    @Test
    @DisplayName("댓글 좋아요")
    void likeCommComment_success() {
        // when
        CommCommentLikeEntity commCommentLikeEntity = entityManager.find(CommCommentLikeEntity.class, new CommCommentLikeId(postId, path, memberId));

        // then
        assertThat(commCommentLikeEntity).isNotNull();
        assertThat(commCommentLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(commCommentLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(commCommentLikeEntity.getCreatedAt()).isNotNull();
        assertThat(commCommentLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("댓글 좋아요 삭제")
    void unlikeCommComment_success() {
        // when
        CommCommentLikeEntity commCommentLikeEntity = entityManager.find(CommCommentLikeEntity.class, new CommCommentLikeId(postId, path, memberId));
        entityManager.remove(commCommentLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        CommCommentLikeEntity deletedEntity = entityManager.find(CommCommentLikeEntity.class, new CommCommentLikeId(postId, path, memberId));
        assertThat(deletedEntity).isNull();
    }
}