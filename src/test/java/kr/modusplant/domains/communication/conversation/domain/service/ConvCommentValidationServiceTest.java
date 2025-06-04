package kr.modusplant.domains.communication.conversation.domain.service;

import jakarta.persistence.EntityManager;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCommentRepository;
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
public class ConvCommentValidationServiceTest implements
        ConvCommentEntityTestUtils, ConvCategoryEntityTestUtils,
        ConvPostEntityTestUtils, SiteMemberEntityTestUtils {

    @InjectMocks
    private final ConvCommentValidationService commentValidationService;

    @Spy
    private final ConvCommentRepository commentRepository;

    private final EntityManager entityManager;

    @Autowired
    public ConvCommentValidationServiceTest(
            ConvCommentValidationService commentValidationService, ConvCommentRepository commentRepository,
            EntityManager entityManager) {
        this.commentValidationService = commentValidationService;
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    private SiteMemberEntity memberEntity;
    private ConvPostEntity postEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntity();
        ConvCategoryEntity category = entityManager.merge(createTestConvCategoryEntity());
        postEntity = createConvPostEntityBuilder()
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
    void validateFoundConvCommentEntityTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
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
                () -> commentValidationService.validateFoundConvCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals("conv comment entity already exists", ex.getMessage());
    }

    @DisplayName("postUlid와 댓글 경로에 해당하는 댓글 데이터가 존재하지 않는지 확인")
    @Test
    void validateNotFoundEntityTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
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
                () -> commentValidationService.validateNotFoundConvCommentEntity(
                        commentEntity.getPostUlid(), commentEntity.getPath()
                )
        );
        assertEquals("conv comment entity not found", ex.getMessage());
    }

}
