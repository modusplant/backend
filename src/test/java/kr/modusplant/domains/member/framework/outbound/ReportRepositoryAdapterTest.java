package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.common.util.domain.aggregate.ProposalOrBugReportTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.ProposalBugReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.CommentAbuseReportJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentIdTestUtils.testActivitySubjectCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils.testActivitySubjectPostId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ReportRepositoryAdapterTest implements PostAbuseReportEntityTestUtils, CommentAbuseReportEntityTestUtils,
        ProposalOrBugReportTestUtils, ProposalBugReportEntityTestUtils {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());

    private final ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final ProposalOrBugReportJpaRepository proposalOrBugReportJpaRepository = Mockito.mock(ProposalOrBugReportJpaRepository.class);
    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final PostAbuseReportJpaRepository postAbuseReportJpaRepository = Mockito.mock(PostAbuseReportJpaRepository.class);
    private final CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommentAbuseReportJpaRepository commentAbuseReportJpaRepository = Mockito.mock(CommentAbuseReportJpaRepository.class);
    private final ProposalOrBugReportJooqRepository proposalOrBugReportJooqRepository = Mockito.mock(ProposalOrBugReportJooqRepository.class);

    private final ReportRepositoryAdapter reportRepositoryAdapter = new ReportRepositoryAdapter(
            applicationEventPublisher, memberJpaRepository, proposalOrBugReportJpaRepository, postJpaRepository, postAbuseReportJpaRepository, commentJpaRepository, commentAbuseReportJpaRepository, proposalOrBugReportJooqRepository);

    @Test
    @DisplayName("보고서 식별자가 존재할 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenExistedReportId_willReturnResponseInProposalOrBugReport() {
        // given
        given(proposalOrBugReportJpaRepository.existsByUlid(any())).willReturn(true);

        // when
        boolean isReportIdExist = reportRepositoryAdapter.isIdExistInProposalOrBugReport(testReportId);

        // then
        assertThat(isReportIdExist).isTrue();
    }

    @Test
    @DisplayName("보고서 식별자가 존재하지 않을 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenNotFoundReportId_willReturnResponseInProposalOrBugReport() {
        // given
        given(proposalOrBugReportJpaRepository.existsByUlid(any())).willReturn(false);

        // when
        boolean isReportIdExist = reportRepositoryAdapter.isIdExistInProposalOrBugReport(testReportId);

        // then
        assertThat(isReportIdExist).isFalse();
    }

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenExistedAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createPublishedPostEntityBuilder().build()));
        given(postAbuseReportJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.of(createPostAbuseReportEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = reportRepositoryAdapter.isMemberAbusePost(testMemberId, testActivitySubjectPostId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createPublishedPostEntityBuilder().build()));
        given(postAbuseReportJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryAdapter.isMemberAbusePost(testMemberId, testActivitySubjectPostId);

        // then
        assertThat(isMemberAbusePost).isFalse();
    }

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbuseComment로 댓글 신고 여부 반환")
    void testIsMemberAbuseComment_givenExistedAbuse_willReturnResponse() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any()))
                .willReturn(Optional.of(createCommentEntityBuilder().build()));
        given(commentAbuseReportJpaRepository.findByMemberIdAndComment(any(), any()))
                .willReturn(Optional.of(createCommentAbuseReportEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = reportRepositoryAdapter.isMemberAbuseComment(testMemberId, testActivitySubjectCommentId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbuseComment로 댓글 신고 여부 반환")
    void testIsMemberAbuseComment_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any()))
                .willReturn(Optional.of(createCommentEntityBuilder().build()));
        given(postAbuseReportJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryAdapter.isMemberAbuseComment(testMemberId, testActivitySubjectCommentId);

        // then
        assertThat(isMemberAbusePost).isFalse();
    }

    @Test
    @DisplayName("존재하는 회원에 대해 reportProposalOrBug 실행 시 건의 및 버그 제보 저장 성공")
    void testReportProposalOrBug_givenValidData_willSaveReport() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));

        // when
        reportRepositoryAdapter.reportProposalOrBug(testMemberId, createProposalOrBugReport());

        // then
        verify(proposalOrBugReportJpaRepository, times(1)).save(any(ProposalOrBugReportEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportProposalOrBug 실행 실패")
    void testReportProposalOrBug_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryAdapter.reportProposalOrBug(testMemberId, createProposalOrBugReport()));

        // then
        verify(proposalOrBugReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 회원 및 게시글에 대해 reportPostAbuse 실행 시 신고 저장 성공")
    void testReportPostAbuse_givenValidData_willSaveReport() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        PostEntity mockPostEntity = mock(PostEntity.class);
        PostAbuseReportEntity mockPostAbuseReportEntity = mock(PostAbuseReportEntity.class);
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.of(mockPostEntity));
        given(postAbuseReportJpaRepository.save(any())).willReturn(mockPostAbuseReportEntity);
        given(mockPostAbuseReportEntity.getCreatedAt()).willReturn(TEST_POST_PUBLISHED_AT);

        // when
        reportRepositoryAdapter.reportPostAbuse(testMemberId, testActivitySubjectPostId);

        // then
        verify(postAbuseReportJpaRepository, times(1)).save(any(PostAbuseReportEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportPostAbuse 실행 실패")
    void testReportPostAbuse_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryAdapter.reportPostAbuse(testMemberId, testActivitySubjectPostId));

        // then
        verify(postAbuseReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 reportPostAbuse 실행 실패")
    void testReportPostAbuse_givenNotFoundPost_willThrowException() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryAdapter.reportPostAbuse(testMemberId, testActivitySubjectPostId));

        // then
        verify(postAbuseReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 회원 및 댓글에 대해 reportCommentAbuse 실행 시 신고 저장 성공")
    void testReportCommentAbuse_givenValidData_willSaveReport() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        CommentEntity commentEntity = mock(CommentEntity.class);

        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(commentJpaRepository.findById(any(CommentCompositeKey.class))).willReturn(Optional.of(commentEntity));

        // when
        reportRepositoryAdapter.reportCommentAbuse(testMemberId, testActivitySubjectCommentId);

        // then
        verify(commentAbuseReportJpaRepository, times(1)).save(any(CommentAbuseReportEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportCommentAbuse 실행 실패")
    void testReportCommentAbuse_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryAdapter.reportCommentAbuse(testMemberId, testActivitySubjectCommentId));

        // then
        verify(commentAbuseReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 reportCommentAbuse 실행 실패")
    void testReportCommentAbuse_givenNotFoundComment_willThrowException() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(commentJpaRepository.findById(any(CommentCompositeKey.class))).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryAdapter.reportCommentAbuse(testMemberId, testActivitySubjectCommentId));

        // then
        verify(commentAbuseReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미지가 존재하는 리포트에 대해 removeProposalOrBugReport 실행 시 S3 삭제 및 아카이빙 수행")
    void testRemoveProposalOrBugReport_givenImagesExist_willDeleteS3AndProcessArchive() {
        // given
        given(proposalOrBugReportJooqRepository.getImageFileKeysFromReportId(any())).willReturn(TEST_POST_CONTENT_IMAGE_FILE_KEYS);

        // when
        reportRepositoryAdapter.removeProposalOrBugReport(testReportId);

        // then
        verify(applicationEventPublisher, times(1)).publishEvent(any(ImagesRemoveTask.class));
    }

    @Test
    @DisplayName("이미지가 없는 리포트에 대해 removeProposalOrBugReport 실행 시 S3 삭제 생략 후 아카이빙 수행")
    void testRemoveProposalOrBugReport_givenImagesEmpty_willProcessArchiveOnly() {
        // given
        given(proposalOrBugReportJooqRepository.getImageFileKeysFromReportId(any())).willReturn(List.of());

        // when
        reportRepositoryAdapter.removeProposalOrBugReport(testReportId);

        // then
        verify(applicationEventPublisher, times(0)).publishEvent(any());
    }
}