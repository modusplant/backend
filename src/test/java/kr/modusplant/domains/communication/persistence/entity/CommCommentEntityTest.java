package kr.modusplant.domains.communication.persistence.entity;

import kr.modusplant.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class CommCommentEntityTest implements CommCommentEntityTestUtils,
        CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    CommCommentEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;}

    @DisplayName("소통 컨텐츠 댓글 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommSecondaryCategoryEntity category = createTestCommSecondaryCategoryEntity();
        entityManager.persist(category);
        CommPostEntity postEntity = createCommPostEntityBuilder()
                .category(category)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
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
