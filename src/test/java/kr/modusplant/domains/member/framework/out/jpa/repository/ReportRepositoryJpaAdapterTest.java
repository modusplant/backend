package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.common.util.domain.aggregate.ProposalOrBugReportTestUtils;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.ProposalBugReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.event.ImageRemoveEvent;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ReportRepositoryJpaAdapterTest implements PostAbuseReportEntityTestUtils, CommentAbuseReportEntityTestUtils, ProposalOrBugReportTestUtils {
    private final String emptyReportId = "empty_report_id";

    private final MockDataProvider mockDataProvider = (MockExecuteContext mockExecuteContext) -> {
        String sql = mockExecuteContext.sql().toLowerCase();

        // 이미지 경로 조회 (select) 모킹
        if (sql.contains("select") && sql.contains("jsonb_array_elements")) {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<String>> result = dsl.newResult(DSL.field("src", String.class));

            // 바인딩된 reportId 값을 기준으로 이미지 없음 케이스 분기
            boolean isEmptyCase = false;
            if (mockExecuteContext.bindings() != null) {
                for (Object binding : mockExecuteContext.bindings()) {
                    if (emptyReportId.equals(binding)) {
                        isEmptyCase = true;
                        break;
                    }
                }
            }

            if (!isEmptyCase) {
                result.add(dsl.newRecord(DSL.field("src", String.class)).values(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1));
            }
            return new MockResult[] { new MockResult(result.size(), result) };
        }

        // 데이터 삽입 및 삭제 (insert, delete) 모킹
        if (sql.contains("insert") || sql.contains("delete")) {
            return new MockResult[] { new MockResult(1) };
        }

        return new MockResult[0];
    };

    private final MockConnection mockConnection = new MockConnection(mockDataProvider);
    private final DSLContext dslContext = DSL.using(mockConnection, SQLDialect.POSTGRES);
    private final ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final ProposalBugReportJpaRepository proposalBugReportJpaRepository = Mockito.mock(ProposalBugReportJpaRepository.class);
    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final PostAbuseReportJpaRepository postAbuRepJpaRepository = Mockito.mock(PostAbuseReportJpaRepository.class);
    private final CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommentAbuseReportJpaRepository commentAbuseReportJpaRepository = Mockito.mock(CommentAbuseReportJpaRepository.class);
    private final ReportRepositoryJpaAdapter reportRepositoryJpaAdapter = new ReportRepositoryJpaAdapter(
            dslContext, applicationEventPublisher, memberJpaRepository, proposalBugReportJpaRepository, postJpaRepository, postAbuRepJpaRepository, commentJpaRepository, commentAbuseReportJpaRepository);

    @Test
    @DisplayName("보고서 식별자가 존재할 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenExistedReportId_willReturnResponse() {
        // given
        given(proposalBugReportJpaRepository.existsByUlid(any())).willReturn(true);

        // when
        boolean isReportIdExist = reportRepositoryJpaAdapter.isIdExist(testReportId);

        // then
        assertThat(isReportIdExist).isTrue();
    }

    @Test
    @DisplayName("보고서 식별자가 존재하지 않을 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenNotFoundReportId_willReturnResponse() {
        // given
        given(proposalBugReportJpaRepository.existsByUlid(any())).willReturn(false);

        // when
        boolean isReportIdExist = reportRepositoryJpaAdapter.isIdExist(testReportId);

        // then
        assertThat(isReportIdExist).isFalse();
    }

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenExistedAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createPostEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.of(createPostAbuseReportEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createPostEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

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
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbuseComment로 댓글 신고 여부 반환")
    void testIsMemberAbuseComment_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any()))
                .willReturn(Optional.of(createCommentEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

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
        reportRepositoryJpaAdapter.reportProposalOrBug(testMemberId, createProposalOrBugReport());

        // then
        verify(proposalBugReportJpaRepository, times(1)).save(any(ProposalBugReportEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportProposalOrBug 실행 실패")
    void testReportProposalOrBug_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryJpaAdapter.reportProposalOrBug(testMemberId, createProposalOrBugReport()));

        // then
        verify(proposalBugReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 회원 및 게시글에 대해 reportPostAbuse 실행 시 신고 저장 성공")
    void testReportPostAbuse_givenValidData_willSaveReport() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        PostEntity mockPost = mock(PostEntity.class);
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.of(mockPost));

        // when
        reportRepositoryJpaAdapter.reportPostAbuse(testMemberId, testTargetPostId);

        // then
        verify(postAbuRepJpaRepository, times(1)).save(any(PostAbuseReportEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportPostAbuse 실행 실패")
    void testReportPostAbuse_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryJpaAdapter.reportPostAbuse(testMemberId, testTargetPostId));

        // then
        verify(postAbuRepJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 reportPostAbuse 실행 실패")
    void testReportPostAbuse_givenNotFoundPost_willThrowException() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryJpaAdapter.reportPostAbuse(testMemberId, testTargetPostId));

        // then
        verify(postAbuRepJpaRepository, never()).save(any());
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
        reportRepositoryJpaAdapter.reportCommentAbuse(testMemberId, testTargetCommentId);

        // then
        verify(commentAbuseReportJpaRepository, times(1)).save(any(CommentAbuseReportEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportCommentAbuse 실행 실패")
    void testReportCommentAbuse_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportRepositoryJpaAdapter.reportCommentAbuse(testMemberId, testTargetCommentId));

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
        assertThrows(NoSuchElementException.class, () -> reportRepositoryJpaAdapter.reportCommentAbuse(testMemberId, testTargetCommentId));

        // then
        verify(commentAbuseReportJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미지가 존재하는 리포트에 대해 removeProposalOrBugReport 실행 시 S3 삭제 및 아카이빙 수행")
    void testRemoveProposalOrBugReport_givenImagesExist_willDeleteS3AndProcessArchive() {
        // given & when
        reportRepositoryJpaAdapter.removeProposalOrBugReport(testReportId);

        // then
        verify(applicationEventPublisher, times(1)).publishEvent(any(ImageRemoveEvent.class));
    }

    @Test
    @DisplayName("이미지가 없는 리포트에 대해 removeProposalOrBugReport 실행 시 S3 삭제 생략 후 아카이빙 수행")
    void testRemoveProposalOrBugReport_givenImagesEmpty_willProcessArchiveOnly() {
        // given
        ReportId reportId = mock(ReportId.class);
        given(reportId.getValue()).willReturn(emptyReportId); // MockDataProvider에서 빈 목록을 반환하도록 처리됨

        // when
        reportRepositoryJpaAdapter.removeProposalOrBugReport(reportId);

        // then
        verify(applicationEventPublisher, times(0)).publishEvent(any());
    }
}