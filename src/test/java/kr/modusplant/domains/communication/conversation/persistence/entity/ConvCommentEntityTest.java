package kr.modusplant.domains.communication.conversation.persistence.entity;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class ConvCommentEntityTest implements ConvCommentEntityTestUtils,
        ConvCategoryEntityTestUtils, ConvPostEntityTestUtils {

    private final ConvCategoryRepository categoryRepository;
    private final TestEntityManager entityManager;

    @Autowired
    ConvCommentEntityTest(ConvCategoryRepository categoryRepository, TestEntityManager entityManager) {
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;}

    @DisplayName("소통 팁 댓글 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        ConvCategoryEntity category = categoryRepository.save(testConvCategoryEntity);
        ConvPostEntity postEntity = createConvPostEntityBuilder()
                .group(category)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
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
