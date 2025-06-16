package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCommentResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCommentRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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
public class ConvCommentApplicationServiceTest implements
        ConvCommentEntityTestUtils, ConvCommentInsertRequestTestUtils, ConvCommentResponseTestUtils,
        ConvCategoryEntityTestUtils, ConvPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final ConvCommentApplicationService commentApplicationService;
    private final ConvCommentRepository commentRepository;
    private final ConvCategoryRepository categoryRepository;
    private final ConvPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public ConvCommentApplicationServiceTest(
            ConvCommentApplicationService commentApplicationService,
            ConvCommentRepository commentRepository, ConvPostRepository postRepository,
            ConvCategoryRepository categoryRepository, SiteMemberRepository memberRepository) {
        this.commentApplicationService = commentApplicationService;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private SiteMemberEntity memberEntity;
    private ConvPostEntity postEntity;
    private ConvCommentEntity commentEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        ConvCategoryEntity category = categoryRepository.save(createTestConvCategoryEntityWithUuid());
        postEntity = createConvPostEntityBuilder()
                .ulid(convPostWithUlid.getUlid())
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        ConvCommentResponse commentResponse = createConvCommentResponse(
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        ConvCommentResponse commentResponse = createConvCommentResponse(
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        ConvCommentResponse commentResponse = createConvCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        ConvCommentResponse commentResponse = createConvCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        ConvCommentResponse commentResponse = createConvCommentResponse(
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        ConvCommentInsertRequest insertRequest = createConvCommentInsertRequest(postEntity.getUlid());

        ConvCommentResponse commentResponse = createConvCommentResponse(
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
        commentEntity = createConvCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        // when
        given(commentRepository.existsByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath())).willReturn(true);
        commentApplicationService.removeByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath());

        // then
        verify(commentRepository).deleteByPostUlidAndPath(commentEntity.getPostEntity().getUlid(), commentEntity.getPath());
    }
}
