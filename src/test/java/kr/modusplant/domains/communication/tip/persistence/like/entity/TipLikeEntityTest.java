package kr.modusplant.domains.communication.tip.persistence.like.entity;

import kr.modusplant.domains.communication.tip.common.util.like.entity.TipLikeEntityTestUtils;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class TipLikeEntityTest implements TipLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String tipPostId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        tipPostId = tipPostWithUlid.getUlid();
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        TipLikeEntity tipLikeEntity = TipLikeEntity.of(tipPostId, memberId);
        entityManager.persistAndFlush(tipLikeEntity);
    }

    @Test
    @DisplayName("팁 게시글 좋아요")
    void likeTipPost_success () {
        // when
        TipLikeEntity tipLikeEntity = entityManager.find(TipLikeEntity.class, new TipLikeId(tipPostId, memberId));

        // then
        assertThat(tipLikeEntity).isNotNull();
        assertThat(tipLikeEntity.getTipPostId()).isEqualTo(tipPostId);
        assertThat(tipLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(tipLikeEntity.getCreatedAt()).isNotNull();
        assertThat(tipLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("팁 게시글 좋아요 삭제")
    void unlikeTipPost_success() {
        // when
        TipLikeEntity tipLikeEntity = entityManager.find(TipLikeEntity.class, new TipLikeId(tipPostId, memberId));
        entityManager.remove(tipLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        TipLikeEntity deletedEntity = entityManager.find(TipLikeEntity.class, new TipLikeId(tipPostId, memberId));
        assertThat(deletedEntity).isNull();
    }
}