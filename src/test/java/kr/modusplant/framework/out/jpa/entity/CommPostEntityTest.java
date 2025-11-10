package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.framework.out.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class CommPostEntityTest implements CommPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    CommPostEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("컨텐츠 게시글 PrePersist")
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = entityManager.merge(createCommPrimaryCategoryEntity());
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = entityManager.merge(createCommSecondaryCategoryEntityBuilder().primaryCategory(commPrimaryCategoryEntity).build());
        CommPostEntity commPost = createCommPostEntityBuilder()
                .primaryCategory(commPrimaryCategoryEntity)
                .secondaryCategory(commSecondaryCategoryEntity)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();

        // when
        entityManager.persist(commPost);
        entityManager.flush();

        // then
        assertThat(commPost.getLikeCount()).isEqualTo(1);
        assertThat(commPost.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글 좋아요 수 증가 테스트")
    void increaseLikeCountTest() {
        CommPostEntity commPost = createCommPostEntityBuilder()
                .likeCount(0)
                .build();

        commPost.increaseLikeCount();

        assertThat(commPost.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 좋아요 수 감소 테스트")
    void decreaseLikeCountTest() {
        CommPostEntity commPost = createCommPostEntityBuilder()
                .likeCount(1)
                .build();

        commPost.decreaseLikeCount();
        assertThat(commPost.getLikeCount()).isEqualTo(0);

        commPost.decreaseLikeCount();
        assertThat(commPost.getLikeCount()).isEqualTo(0);
    }
}