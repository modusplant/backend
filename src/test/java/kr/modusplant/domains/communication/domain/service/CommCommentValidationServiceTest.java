package kr.modusplant.domains.communication.domain.service;

import jakarta.persistence.EntityManager;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.error.CommentExistsException;
import kr.modusplant.domains.communication.error.CommentNotFoundException;
import kr.modusplant.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPrimaryCategoryEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.communication.persistence.repository.CommCommentRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
@Transactional
public class CommCommentValidationServiceTest implements
        CommCommentEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils,
        CommPostEntityTestUtils, SiteMemberEntityTestUtils {

    @InjectMocks
    private final CommCommentValidationService commentValidationService;

    @Spy
    private final CommCommentRepository commentRepository;

    private final EntityManager entityManager;

    @Autowired
    public CommCommentValidationServiceTest(
            CommCommentValidationService commentValidationService, CommCommentRepository commentRepository,
            EntityManager entityManager) {
        this.commentValidationService = commentValidationService;
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    private SiteMemberEntity memberEntity;
    private CommPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntity();
        CommPrimaryCategoryEntity primaryCategory = entityManager.merge(createTestCommPrimaryCategoryEntity());
        CommSecondaryCategoryEntity secondaryCategory = entityManager.merge(createTestCommSecondaryCategoryEntity());
        postEntity = createCommPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
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
    void validateExistedCommCommentEntityTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        entityManager.persist(commentEntity);
        entityManager.flush();

        // when
        given(commentRepository.existsByPostUlidAndPath(commentEntity.getPostUlid(), commentEntity.getPath())).willReturn(true);

        // then
        CommentExistsException ex = assertThrows(
                CommentExistsException.class,
                () -> commentValidationService.validateExistedCommCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals(new CommentExistsException().getMessage(), ex.getMessage());
    }

    @DisplayName("postUlid와 댓글 경로에 해당하는 댓글 데이터가 존재하지 않는지 확인")
    @Test
    void validateNotFoundEntityTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        entityManager.persist(commentEntity);
        entityManager.flush();

        // when
        given(commentRepository.existsByPostUlidAndPath(commentEntity.getPostUlid(), commentEntity.getPath())).willReturn(false);

        // then
        CommentNotFoundException ex = assertThrows(
                CommentNotFoundException.class,
                () -> commentValidationService.validateNotFoundCommCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals(new CommentNotFoundException().getMessage(), ex.getMessage());
    }
}
