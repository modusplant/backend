package kr.modusplant.domains.communication.qna.persistence.entity;

import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
//import kr.modusplant.domains.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class QnaPostEntityTest implements QnaPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    QnaPostEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("팁 게시글 PrePersist")
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .group(plantGroup)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        // when
        entityManager.persist(qnaPost);
        entityManager.flush();

        // then
        assertThat(qnaPost.getLikeCount()).isEqualTo(1);
        assertThat(qnaPost.getViewCount()).isEqualTo(1L);
        assertThat(qnaPost.getIsDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("팁 게시글 PreUpdate")
    void preUpdate() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .group(plantGroup)
                .authMember(member)
                .createMember(member)
                .build();
        entityManager.persist(qnaPost);

        // when
        qnaPost.updateViewCount(null);
        qnaPost.updateIsDeleted(null);
        entityManager.flush();

        // then
        assertThat(qnaPost.getViewCount()).isEqualTo(0L);
        assertThat(qnaPost.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 수 증가 테스트")
    void increaseLikeCountTest() {
        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .likeCount(0)
                .build();

        qnaPost.increaseLikeCount();

        assertThat(qnaPost.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 수 감소 테스트")
    void decreaseLikeCountTest() {
        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .likeCount(1)
                .build();

        qnaPost.decreaseLikeCount();
        assertThat(qnaPost.getLikeCount()).isEqualTo(0);

        qnaPost.decreaseLikeCount();
        assertThat(qnaPost.getLikeCount()).isEqualTo(0);
    }
}