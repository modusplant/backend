package kr.modusplant.domains.communication.tip.persistence.entity;

import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class TipCommentEntityTest implements TipCommentEntityTestUtils,
        TipCategoryEntityTestUtils, TipPostEntityTestUtils {

    private final TipCategoryRepository categoryRepository;
    private final TestEntityManager entityManager;

    @Autowired
    TipCommentEntityTest(TipCategoryRepository categoryRepository, TestEntityManager entityManager) {
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;}

    @DisplayName("소통 팁 댓글 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        TipCategoryEntity category = categoryRepository.save(testTipCategoryEntity);
        TipPostEntity postEntity = createTipPostEntityBuilder()
                .group(category)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
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
