package kr.modusplant.domains.communication.tip.persistence.entity;

import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class TipPostEntityTest implements TipPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    TipPostEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("팁 게시글 PrePersist")
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        TipCategoryEntity tipCategoryEntity = entityManager.merge(createTestTipCategoryEntity());
        TipPostEntity tipPost = createTipPostEntityBuilder()
                .category(tipCategoryEntity)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        // when
        entityManager.persist(tipPost);
        entityManager.flush();

        // then
        assertThat(tipPost.getLikeCount()).isEqualTo(1);
        assertThat(tipPost.getViewCount()).isEqualTo(1L);
        assertThat(tipPost.getIsDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("팁 게시글 PreUpdate")
    void preUpdate() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        TipPostEntity tipPost = createTipPostEntityBuilder()
                .category(createTestTipCategoryEntity())
                .authMember(member)
                .createMember(member)
                .build();
        entityManager.persist(tipPost);

        // when
        tipPost.updateViewCount(null);
        tipPost.updateIsDeleted(null);
        entityManager.flush();

        // then
        assertThat(tipPost.getViewCount()).isEqualTo(0L);
        assertThat(tipPost.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 수 증가 테스트")
    void increaseLikeCountTest() {
        TipPostEntity tipPost = createTipPostEntityBuilder()
                .likeCount(0)
                .build();

        tipPost.increaseLikeCount();

        assertThat(tipPost.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 수 감소 테스트")
    void decreaseLikeCountTest() {
        TipPostEntity tipPost = createTipPostEntityBuilder()
                .likeCount(1)
                .build();

        tipPost.decreaseLikeCount();
        assertThat(tipPost.getLikeCount()).isEqualTo(0);

        tipPost.decreaseLikeCount();
        assertThat(tipPost.getLikeCount()).isEqualTo(0);
    }
}