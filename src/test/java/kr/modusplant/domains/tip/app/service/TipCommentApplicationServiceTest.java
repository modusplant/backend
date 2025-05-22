package kr.modusplant.domains.tip.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.common.util.app.http.request.TipCommentInsertRequestTestUtils;
import kr.modusplant.domains.tip.common.util.app.http.response.TipCommentResponseTestUtils;
import kr.modusplant.domains.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.tip.domain.service.TipCommentValidationService;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipCommentRepository;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DomainsServiceOnlyContext
@Transactional
public class TipCommentApplicationServiceTest implements
        TipCommentEntityTestUtils, TipCommentInsertRequestTestUtils, TipCommentResponseTestUtils,
        TipPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final TipCommentApplicationService commentApplicationService;
    private final TipCommentRepository commentRepository;
    private final TipPostRepository postRepository;
    private final SiteMemberRepository memberRepository;
    private final TipCommentValidationService commentValidationService;

    @Autowired
    public TipCommentApplicationServiceTest(
            TipCommentApplicationService commentApplicationService, TipCommentValidationService commentValidationService,
            TipCommentRepository commentRepository, TipPostRepository postRepository,
            SiteMemberRepository memberRepository) {
        this.commentApplicationService = commentApplicationService;
        this.commentValidationService = commentValidationService;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private SiteMemberEntity memberEntity;
    private TipPostEntity postEntity;
    private TipCommentEntity commentEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        postEntity = createTipPostEntityBuilder()
                .ulid((String) generator.generate(null, null))
                .group(plantGroup)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .recommendationNumber(1)
                .viewCount(1)
                .isDeleted(true)
                .build();
    }

    @DisplayName("게시글 엔티티로 댓글 가져오기")
    @Test
    void getByPostEntityTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        TipCommentResponse commentResponse = createTipCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getUuid()
        );

        // when
        given(postRepository.findByUlid(postEntity.getUlid())).willReturn(Optional.of(postEntity));
        given(commentRepository.findByPostEntity(postEntity)).willReturn(List.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByPostEntity(postEntity))
        .isEqualTo(List.of(commentResponse));
    }

    @DisplayName("인증된 사용자 엔티티로 댓글 가져오기")
    @Test
    void getByAuthMemberTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        TipCommentResponse commentResponse = createTipCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getUuid()
        );

        // when
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(commentRepository.findByAuthMember(memberEntity)).willReturn(List.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByAuthMember(memberEntity))
                .isEqualTo(List.of(commentResponse));
    }

    @DisplayName("생성한 사용자 엔티티로 댓글 가져오기")
    @Test
    void getByCreateMemberTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        TipCommentResponse commentResponse = createTipCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getUuid()
        );

        // when
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(commentRepository.findByCreateMember(memberEntity)).willReturn(List.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByCreateMember(memberEntity))
                .isEqualTo(List.of(commentResponse));
    }

    @DisplayName("댓글 내용으로 댓글 가져오기")
    @Test
    void getByContentTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        TipCommentResponse commentResponse = createTipCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getUuid()
        );

        // when
        given(commentRepository.findByContent(commentEntity.getContent())).willReturn(List.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByContent(commentEntity.getContent()))
                .isEqualTo(List.of(commentResponse));
    }

    @DisplayName("게시글의 ulid와 댓글 경로로 댓글 가져오기")
    @Test
    void getByPostUlidAndMaterializedPathTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        TipCommentResponse commentResponse = createTipCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getUuid()
        );

        // when
        given(commentRepository.findByPostUlidAndMaterializedPath(
                commentEntity.getPostEntity().getUlid(), commentEntity.getMaterializedPath()
        )).willReturn(Optional.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByPostUlidAndMaterializedPath(
                commentEntity.getPostEntity().getUlid(), commentEntity.getMaterializedPath()
        ))
                .isEqualTo(Optional.of(commentResponse));
    }

    @DisplayName("댓글 db에 삽입하기")
    @Test
    void insertTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        TipCommentInsertRequest insertRequest = createTipCommentInsertRequest(
                postEntity.getUlid(), memberEntity.getUuid()
        );

        TipCommentResponse commentResponse = createTipCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getUuid()
        );

        // when
        given(commentRepository.findByPostUlidAndMaterializedPath(
                postEntity.getUlid(), commentEntity.getMaterializedPath()
        )).willReturn(Optional.empty());
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(postRepository.findByUlid(postEntity.getUlid())).willReturn(Optional.of(postEntity));
        given(commentRepository.save(commentEntity)).willReturn(commentEntity);

        // then
        assertThat(commentApplicationService.insert(insertRequest))
                .isEqualTo(commentResponse);
    }

    @DisplayName("게시글 ulid와 댓글 경로로 댓글 삭제하기")
    @Test
    void removeByPostUlidAndMaterializedPathTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        // when
        given(commentRepository.findByPostUlidAndMaterializedPath(commentEntity.getPostEntity().getUlid(), commentEntity.getMaterializedPath()))
                .willReturn(Optional.of(commentEntity));
        commentApplicationService
                .removeByPostUlidAndMaterializedPath(commentEntity.getPostEntity().getUlid(), commentEntity.getMaterializedPath());

        // then
        verify(commentRepository).deleteByPostUlidAndMaterializedPath(commentEntity.getPostEntity().getUlid(), commentEntity.getMaterializedPath());
    }
}
