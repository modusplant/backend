package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommCommentEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCommentInsertRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCommentResponseTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.shared.persistence.common.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DomainsServiceWithoutValidationServiceContext
public class CommCommentApplicationServiceTest implements
        CommCommentEntityTestUtils, CommCommentInsertRequestTestUtils, CommCommentResponseTestUtils,
        CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final CommCommentApplicationService commentApplicationService;
    private final CommCommentJpaRepository commentRepository;
    private final CommSecondaryCategoryJpaRepository categoryRepository;
    private final CommPostJpaRepository postRepository;
    private final SiteMemberJpaRepository memberRepository;

    @Autowired
    public CommCommentApplicationServiceTest(
            CommCommentApplicationService commentApplicationService,
            CommCommentJpaRepository commentRepository, CommPostJpaRepository postRepository,
            CommSecondaryCategoryJpaRepository categoryRepository, SiteMemberJpaRepository memberRepository) {
        this.commentApplicationService = commentApplicationService;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private SiteMemberEntity memberEntity;
    private CommPostEntity postEntity;
    private CommCommentEntity commentEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        CommSecondaryCategoryEntity category = categoryRepository.save(createTestCommSecondaryCategoryEntityWithUuid());
        postEntity = createCommPostEntityBuilder()
                .ulid(TEST_COMM_POST_ULID)
                .secondaryCategory(category)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();
    }

    @DisplayName("게시글 엔티티로 댓글 가져오기")
    @Test
    void getByPostEntityTest() {
        // given
        commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        CommCommentResponse commentResponse = createCommCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
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
        commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        CommCommentResponse commentResponse = createCommCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
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
        commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        CommCommentResponse commentResponse = createCommCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
        );

        // when
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(commentRepository.findByCreateMember(memberEntity)).willReturn(List.of(commentEntity));

        // then
        assertThat(commentApplicationService.getByCreateMember(memberEntity))
                .isEqualTo(List.of(commentResponse));
    }

    @DisplayName("게시글의 ulid와 댓글 경로로 댓글 가져오기")
    @Test
    void getByPostUlidAndPathTest() {
        // given
        commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        CommCommentResponse commentResponse = createCommCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
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
        commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        CommCommentInsertRequest insertRequest = createCommCommentInsertRequest(postEntity.getUlid());

        CommCommentResponse commentResponse = createCommCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
        );

        // when
        given(commentRepository.existsByPostUlidAndPath(postEntity.getUlid(), commentEntity.getPath())).willReturn(false);
        given(memberRepository.existsByUuid(memberEntity.getUuid())).willReturn(true);
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(postRepository.findByUlid(postEntity.getUlid())).willReturn(Optional.of(postEntity));
        given(commentRepository.save(commentEntity)).willReturn(commentEntity);

        // then
        assertThat(commentApplicationService.insert(insertRequest, memberEntity.getUuid()))
                .isEqualTo(commentResponse);
    }

    @DisplayName("게시글 ulid와 댓글 경로로 댓글 삭제하기")
    @Test
    void removeByPostUlidAndPathTest() {
        // given
        commentEntity = createCommCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        // when
        given(commentRepository.existsByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath()))
                .willReturn(true);
        commentApplicationService
                .removeByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath());

        // then
        verify(commentRepository).deleteByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath());
    }
}
