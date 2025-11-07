package kr.modusplant.framework.out.jpa.entity;

import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class CommCommentEntityTest implements CommCommentEntityTestUtils,
        CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    CommCommentEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;}

    @DisplayName("컨텐츠 댓글 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommPrimaryCategoryEntity primaryCategory = createCommPrimaryCategoryEntity();
        CommSecondaryCategoryEntity secondaryCategory = createCommSecondaryCategoryEntityBuilder().primaryCategoryEntity(primaryCategory).build();
        entityManager.persist(primaryCategory);
        entityManager.persist(secondaryCategory);
        CommPostEntity postEntity = createCommPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();

        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(member)
                .createMember(member)
                .isDeleted(true)
                .build();

        // when
        entityManager.persist(postEntity);
        entityManager.persist(commentEntity);
        entityManager.flush();

        // then
        assertThat(commentEntity.getIsDeleted()).isEqualTo(true);
    }
}
