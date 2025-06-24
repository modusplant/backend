package kr.modusplant.domains.communication.qna.persistence.entity;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaLikeEntityTestUtils;
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
public class QnaLikeEntityTest implements QnaLikeEntityTestUtils {

    @Autowired
    private TestEntityManager entityManager;

    private String postId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        // given
        postId = testQnaPostWithUlid.getUlid();
        memberId = createMemberBasicUserEntityWithUuid().getUuid();

        QnaLikeEntity qnaLikeEntity = QnaLikeEntity.of(postId, memberId);
        entityManager.persistAndFlush(qnaLikeEntity);
    }

    @Test
    @DisplayName("Q&A 게시글 좋아요")
    void likeQnaPost_success () {
        // when
        QnaLikeEntity qnaLikeEntity = entityManager.find(QnaLikeEntity.class, new QnaLikeId(postId, memberId));

        // then
        assertThat(qnaLikeEntity).isNotNull();
        assertThat(qnaLikeEntity.getPostId()).isEqualTo(postId);
        assertThat(qnaLikeEntity.getMemberId()).isEqualTo(memberId);
        assertThat(qnaLikeEntity.getCreatedAt()).isNotNull();
        assertThat(qnaLikeEntity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Q&A 게시글 좋아요 삭제")
    void unlikeQnaPost_success() {
        // when
        QnaLikeEntity qnaLikeEntity = entityManager.find(QnaLikeEntity.class, new QnaLikeId(postId, memberId));
        entityManager.remove(qnaLikeEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        QnaLikeEntity deletedEntity = entityManager.find(QnaLikeEntity.class, new QnaLikeId(postId, memberId));
        assertThat(deletedEntity).isNull();
    }
}