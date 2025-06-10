package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCommentResponseTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCommentRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
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
public class QnaCommentApplicationServiceTest implements
        QnaCommentEntityTestUtils, QnaCommentInsertRequestTestUtils, QnaCommentResponseTestUtils,
        QnaCategoryEntityTestUtils, QnaPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final QnaCommentApplicationService commentApplicationService;
    private final QnaCommentRepository commentRepository;
    private final QnaCategoryRepository categoryRepository;
    private final QnaPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public QnaCommentApplicationServiceTest(
            QnaCommentApplicationService commentApplicationService,
            QnaCommentRepository commentRepository, QnaPostRepository postRepository,
            QnaCategoryRepository categoryRepository, SiteMemberRepository memberRepository) {
        this.commentApplicationService = commentApplicationService;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private SiteMemberEntity memberEntity;
    private QnaPostEntity postEntity;
    private QnaCommentEntity commentEntity;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberBasicUserEntityWithUuid();
        QnaCategoryEntity category = categoryRepository.save(createTestQnaCategoryEntityWithUuid());
        postEntity = createQnaPostEntityBuilder()
                .ulid(qnaPostWithUlid.getUlid())
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
        commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        QnaCommentResponse commentResponse = createQnaCommentResponse(
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
        commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        QnaCommentResponse commentResponse = createQnaCommentResponse(
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
        commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        QnaCommentResponse commentResponse = createQnaCommentResponse(
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
        commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        QnaCommentResponse commentResponse = createQnaCommentResponse(
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
        commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        QnaCommentResponse commentResponse = createQnaCommentResponse(
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
        commentEntity = createQnaCommentEntityBuilder()
                .postEntity(postEntity)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .isDeleted(true)
                .build();

        QnaCommentInsertRequest insertRequest = createQnaCommentInsertRequest(
                postEntity.getUlid(), memberEntity.getUuid()
        );

        QnaCommentResponse commentResponse = createQnaCommentResponse(
                postEntity.getUlid(), memberEntity.getUuid(), memberEntity.getNickname()
        );

        // when
        given(commentRepository.existsByPostUlidAndPath(postEntity.getUlid(), commentEntity.getPath())).willReturn(false);
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
        commentEntity = createQnaCommentEntityBuilder()
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
