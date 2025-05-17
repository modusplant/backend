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
import static org.junit.jupiter.api.Assertions.*;

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
                .recommendationNumber(1)
                .viewCount(1)
                .isDeleted(true)
                .build();

        // when
        entityManager.persist(tipPost);
        entityManager.flush();

        // then
        assertThat(tipPost.getRecommendationNumber()).isEqualTo(1);
        assertThat(tipPost.getViewCount()).isEqualTo(1);
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
        tipPost.updateRecommendationNumber(null);
        tipPost.updateViewCount(null);
        tipPost.updateIsDeleted(null);
        entityManager.flush();

        // then
        assertThat(tipPost.getRecommendationNumber()).isEqualTo(0);
        assertThat(tipPost.getViewCount()).isEqualTo(0);
        assertThat(tipPost.getIsDeleted()).isEqualTo(false);
    }
}