package kr.modusplant.domains.communication.tip.domain.service;

import jakarta.persistence.EntityManager;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCommentRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndMatePathException;
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
public class TipCommentValidationServiceTest implements
        TipCommentEntityTestUtils, TipCategoryEntityTestUtils,
        TipPostEntityTestUtils, SiteMemberEntityTestUtils {

    @InjectMocks
    private final TipCommentValidationService commentValidationService;

    @Spy
    private final TipCommentRepository commentRepository;

    private final EntityManager entityManager;

    @Autowired
    public TipCommentValidationServiceTest(
            TipCommentValidationService commentValidationService, TipCommentRepository commentRepository,
            EntityManager entityManager) {
        this.commentValidationService = commentValidationService;
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    private SiteMemberEntity memberEntity;
    private TipPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntity();
        TipCategoryEntity category = entityManager.merge(createTestTipCategoryEntity());
        postEntity = createTipPostEntityBuilder()
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
    void validateFoundTipCommentEntityTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
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
                () -> commentValidationService.validateFoundTipCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals("tip comment entity already exists", ex.getMessage());
    }

    @DisplayName("postUlid와 댓글 경로에 해당하는 댓글 데이터가 존재하지 않는지 확인")
    @Test
    void validateNotFoundEntityTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
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
                () -> commentValidationService.validateNotFoundTipCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals("tip comment entity not found", ex.getMessage());
    }

}
