package kr.modusplant.domains.communication.conversation.persistence.entity;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvLikeEntityTestUtils;
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
public class ConvLikeEntityTest implements ConvLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = testConvPostWithUlid.getUlid();
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        ConvLikeEntity convLikeEntity = ConvLikeEntity.of(postId, memberId);
        entityManager.persistAndFlush(convLikeEntity);
    }

    @Test
    @DisplayName("대화 게시글 좋아요")
    void likeConvPost_success () {
        // when
        ConvLikeEntity convLikeEntity = entityManager.find(ConvLikeEntity.class, new ConvLikeId(postId, memberId));

        // then
        assertThat(convLikeEntity).isNotNull();
        assertThat(convLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(convLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(convLikeEntity.getCreatedAt()).isNotNull();
        assertThat(convLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("대화 게시글 좋아요 삭제")
    void unlikeConvPost_success() {
        // when
        ConvLikeEntity convLikeEntity = entityManager.find(ConvLikeEntity.class, new ConvLikeId(postId, memberId));
        entityManager.remove(convLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        ConvLikeEntity deletedEntity = entityManager.find(ConvLikeEntity.class, new ConvLikeId(postId, memberId));
        assertThat(deletedEntity).isNull();
    }
}