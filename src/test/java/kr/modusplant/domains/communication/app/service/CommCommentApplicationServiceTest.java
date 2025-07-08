package kr.modusplant.domains.communication.app.service;

import kr.modusplant.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.domains.communication.common.util.app.http.request.CommCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.common.util.app.http.response.CommCommentResponseTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.communication.persistence.repository.CommCommentRepository;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DomainsServiceWithoutValidationServiceContext
public class CommCommentApplicationServiceTest implements
        CommCommentEntityTestUtils, CommCommentInsertRequestTestUtils, CommCommentResponseTestUtils,
        CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final CommCommentApplicationService commentApplicationService;
    private final CommCommentRepository commentRepository;
    private final CommSecondaryCategoryRepository categoryRepository;
    private final CommPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public CommCommentApplicationServiceTest(
            CommCommentApplicationService commentApplicationService,
            CommCommentRepository commentRepository, CommPostRepository postRepository,
            CommSecondaryCategoryRepository categoryRepository, SiteMemberRepository memberRepository) {
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
                .ulid(TEST_COMM_POST_WITH_ULID.getUlid())
                .category(category)
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
