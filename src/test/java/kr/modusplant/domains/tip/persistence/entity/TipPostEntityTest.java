package kr.modusplant.domains.tip.persistence.entity;

import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
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
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        TipPostEntity tipPost = createTipPostEntityBuilder()
                .group(plantGroup)
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
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        TipPostEntity tipPost = createTipPostEntityBuilder()
                .group(plantGroup)
                .authMember(member)
                .createMember(member)
                .build();
        entityManager.persist(tipPost);

        // when
        tipPost.updateLikeCount(null);
        tipPost.updateViewCount(null);
        tipPost.updateIsDeleted(null);
        entityManager.flush();

        // then
        assertThat(tipPost.getLikeCount()).isEqualTo(0);
        assertThat(tipPost.getViewCount()).isEqualTo(0L);
        assertThat(tipPost.getIsDeleted()).isEqualTo(false);
    }
}