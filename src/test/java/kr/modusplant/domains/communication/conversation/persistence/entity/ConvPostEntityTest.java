package kr.modusplant.domains.communication.conversation.persistence.entity;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class ConvPostEntityTest implements ConvPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    ConvPostEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("팁 게시글 PrePersist")
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        ConvPostEntity convPost = createConvPostEntityBuilder()
                .group(plantGroup)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        // when
        entityManager.persist(convPost);
        entityManager.flush();

        // then
        assertThat(convPost.getLikeCount()).isEqualTo(1);
        assertThat(convPost.getViewCount()).isEqualTo(1L);
        assertThat(convPost.getIsDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("팁 게시글 PreUpdate")
    void preUpdate() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        ConvPostEntity convPost = createConvPostEntityBuilder()
                .group(plantGroup)
                .authMember(member)
                .createMember(member)
                .build();
        entityManager.persist(convPost);

        // when
        convPost.updateViewCount(null);
        convPost.updateIsDeleted(null);
        entityManager.flush();

        // then
        assertThat(convPost.getViewCount()).isEqualTo(0L);
        assertThat(convPost.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 수 증가 테스트")
    void increaseLikeCountTest() {
        ConvPostEntity convPost = createConvPostEntityBuilder()
                .likeCount(0)
                .build();

        convPost.increaseLikeCount();

        assertThat(convPost.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 수 감소 테스트")
    void decreaseLikeCountTest() {
        ConvPostEntity convPost = createConvPostEntityBuilder()
                .likeCount(1)
                .build();

        convPost.decreaseLikeCount();
        assertThat(convPost.getLikeCount()).isEqualTo(0);

        convPost.decreaseLikeCount();
        assertThat(convPost.getLikeCount()).isEqualTo(0);
    }
}