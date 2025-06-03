package kr.modusplant.domains.communication.qna.persistence.entity;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
public class QnaCommentEntityTest implements QnaCommentEntityTestUtils,
        QnaCategoryEntityTestUtils, QnaPostEntityTestUtils {

    private final QnaCategoryRepository categoryRepository;
    private final TestEntityManager entityManager;

    @Autowired
    QnaCommentEntityTest(QnaCategoryRepository categoryRepository, TestEntityManager entityManager) {
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;}

    @DisplayName("소통 팁 댓글 PrePersist")
    @Test
    void prePersist() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        QnaCategoryEntity category = categoryRepository.save(testQnaCategoryEntity);
        QnaPostEntity postEntity = createQnaPostEntityBuilder()
                .group(category)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
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
