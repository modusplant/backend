package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCommentResponseTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCommentRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
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
        TipCategoryEntityTestUtils, TipPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final TipCommentApplicationService commentApplicationService;
    private final TipCommentRepository commentRepository;
    private final TipCategoryRepository categoryRepository;
    private final TipPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public TipCommentApplicationServiceTest(
            TipCommentApplicationService commentApplicationService,
            TipCommentRepository commentRepository, TipPostRepository postRepository,
            TipCategoryRepository categoryRepository, SiteMemberRepository memberRepository) {
        this.commentApplicationService = commentApplicationService;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private SiteMemberEntity memberEntity;
    private TipPostEntity postEntity;
    private TipCommentEntity commentEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        TipCategoryEntity category = categoryRepository.save(testTipCategoryEntity);
        postEntity = createTipPostEntityBuilder()
                .ulid(tipPostWithUlid.getUlid())
                .group(category)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .likeCount(1)
                .viewCount(1L)
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
    void getByPostUlidAndPathTest() {
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
        given(commentRepository.findByPostUlidAndPath(
                commentEntity.getPostEntity().getUlid(), commentEntity.getPath()
        )).willReturn(Optional.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByPostUlidAndPath(
                commentEntity.getPostEntity().getUlid(), commentEntity.getPath()
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
        given(commentRepository.findByPostUlidAndPath(
                postEntity.getUlid(), commentEntity.getPath()
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
    void removeByPostUlidAndPathTest() {
        // given
        commentEntity = createTipCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        // when
        given(commentRepository.findByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath()))
                .willReturn(Optional.of(commentEntity));
        commentApplicationService
                .removeByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath());

        // then
        verify(commentRepository).deleteByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath());
    }
}
