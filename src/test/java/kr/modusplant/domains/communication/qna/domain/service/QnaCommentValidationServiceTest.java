package kr.modusplant.domains.communication.qna.domain.service;

import jakarta.persistence.EntityManager;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndMatePathException;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCommentRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
@Transactional
public class QnaCommentValidationServiceTest implements
        QnaCommentEntityTestUtils, QnaCategoryEntityTestUtils,
        QnaPostEntityTestUtils, SiteMemberEntityTestUtils {

    @InjectMocks
    private final QnaCommentValidationService commentValidationService;

    @Spy
    private final QnaCommentRepository commentRepository;

    private final EntityManager entityManager;

    @Autowired
    public QnaCommentValidationServiceTest(
            QnaCommentValidationService commentValidationService, QnaCommentRepository commentRepository,
            EntityManager entityManager) {
        this.commentValidationService = commentValidationService;
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    private SiteMemberEntity memberEntity;
    private QnaPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntity();
        QnaCategoryEntity category = entityManager.merge(createTestQnaCategoryEntity());
        postEntity = createQnaPostEntityBuilder()
                .category(category)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        entityManager.persist(memberEntity);
        entityManager.persist(postEntity);
        entityManager.flush();
    }

    @DisplayName("postUlid와 구체화된 경로에 해당하는 댓글 데이터가 존재하는지 확인")
    @Test
    void validateFoundQnaCommentEntityTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        entityManager.persist(commentEntity);
        entityManager.flush();

        // when
        given(commentRepository.findByPostUlidAndPath(
                commentEntity.getPostUlid(), commentEntity.getPath()
        )).willReturn(Optional.of(commentEntity));

        // then
        EntityExistsWithPostUlidAndMatePathException ex = assertThrows(
                EntityExistsWithPostUlidAndMatePathException.class,
                () -> commentValidationService.validateFoundQnaCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals("qna comment entity already exists", ex.getMessage());
    }

    @DisplayName("postUlid와 댓글 경로에 해당하는 댓글 데이터가 존재하지 않는지 확인")
    @Test
    void validateNotFoundEntityTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        entityManager.persist(commentEntity);
        entityManager.flush();

        // when
        given(commentRepository.findByPostUlidAndPath(
                commentEntity.getPostUlid(), commentEntity.getPath()
        )).willReturn(Optional.empty());

        // then
        EntityNotFoundWithPostUlidAndMatePathException ex = assertThrows(
                EntityNotFoundWithPostUlidAndMatePathException.class,
                () -> commentValidationService.validateNotFoundQnaCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals("qna comment entity not found", ex.getMessage());
    }

}
