package kr.modusplant.framework.out.persistence.entity;

import kr.modusplant.framework.out.persistence.entity.compositekey.CommPostLikeId;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommLikeEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class CommLikeEntityTest implements CommLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = TEST_COMM_POST_WITH_ULID.getUlid();
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        CommLikeEntity commLikeEntity = CommLikeEntity.of(postId, memberId);
        entityManager.persistAndFlush(commLikeEntity);
    }

    @Test
    @DisplayName("컨텐츠 게시글 좋아요")
    void likeCommPost_success () {
        // when
        CommLikeEntity commLikeEntity = entityManager.find(CommLikeEntity.class, new CommPostLikeId(postId, memberId));

        // then
        assertThat(commLikeEntity).isNotNull();
        assertThat(commLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(commLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(commLikeEntity.getCreatedAt()).isNotNull();
        assertThat(commLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("컨텐츠 게시글 좋아요 삭제")
    void unlikeCommPost_success() {
        // when
        CommLikeEntity commLikeEntity = entityManager.find(CommLikeEntity.class, new CommPostLikeId(postId, memberId));
        entityManager.remove(commLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        CommLikeEntity deletedEntity = entityManager.find(CommLikeEntity.class, new CommPostLikeId(postId, memberId));
        assertThat(deletedEntity).isNull();
    }
}