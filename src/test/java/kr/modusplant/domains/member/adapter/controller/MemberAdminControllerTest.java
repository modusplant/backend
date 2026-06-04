package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.event.CommentAbuseReportApproveEvent;
import kr.modusplant.domains.member.domain.event.PostAbuseReportApproveEvent;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.CommentAbuseReportGetRecord;
import kr.modusplant.domains.member.usecase.record.PostAbuseReportGetRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportGetRecord;
import kr.modusplant.domains.member.usecase.response.CommentAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.PostAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportDashboardResponse;
import kr.modusplant.shared.exception.ExistsValueException;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.usecase.model.read.CommentAbuseReportDashboardReadModelTestUtils.testCommentAbuseReportDashboardReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.CommentAbuseReportDashboardReadModelTestUtils.testCommentAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.record.CommentAbuseReportApproveRecordTestUtils.testCommentAbuseReportApproveRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.CommentAbuseReportDismissRecordTestUtils.testCommentAbuseReportDismissRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.CommentAbuseReportGetRecordTestUtils.testCommentAbuseReportGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportApproveRecordTestUtils.testPostAbuseReportApproveRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportDismissRecordTestUtils.testPostAbuseReportDismissRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportGetRecordTestUtils.testPostAbuseReportGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportCheckRecordTestUtils.testProposalOrBugReportCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordGetTestUtils.testProposalOrBugReportGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRemoveRecordTestUtils.testProposalOrBugReportRemoveRecord;
import static kr.modusplant.domains.member.common.util.usecase.response.CommentAbuseDashboardResponseTestUtils.testCommentAbuseReportDashboardResponseWithFalseHasNext;
import static kr.modusplant.domains.member.common.util.usecase.response.CommentAbuseDashboardResponseTestUtils.testCommentAbuseReportDashboardResponseWithTrueHasNext;
import static kr.modusplant.domains.member.common.util.usecase.response.PostAbuseDashboardResponseTestUtils.testPostAbuseReportDashboardResponseWithFalseHasNext;
import static kr.modusplant.domains.member.common.util.usecase.response.PostAbuseDashboardResponseTestUtils.testPostAbuseReportDashboardResponseWithTrueHasNext;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportDashboardResponseTestUtils.testProposalOrBugReportDashboardResponseWithFalseHasNext;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportDashboardResponseTestUtils.testProposalOrBugReportDashboardResponseWithTrueHasNext;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberAdminControllerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    @SuppressWarnings("unused")
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());

    private final MemberValidationHelper memberValidationHelper = Mockito.mock(MemberValidationHelper.class);
    private final ReportRepository reportRepository = Mockito.mock(ReportRepository.class);
    private final ReportDashboardRepository reportDashboardRepository = Mockito.mock(ReportDashboardRepository.class);
    private final ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private final MemberAdminController memberAdminController = new MemberAdminController(memberValidationHelper, reportRepository, reportDashboardRepository, applicationEventPublisher);

    @Test
    @DisplayName("lastReportUlid가 null이 아닐 때 getProposalOrBug로 건의 및 버그 제보 현황 응답 반환")
    void testGetProposalOrBug_givenValidLastReportUlid_willReturnResponse() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfReportExists(any());
        given(reportDashboardRepository.getProposalOrBugReports(any(), any(), any())).willReturn(testProposalOrBugReportDashboardCheckedReadModelList);

        // when
        ProposalOrBugReportDashboardResponse response = memberAdminController.getProposalOrBug(testProposalOrBugReportGetRecord);

        // then
        verify(reportDashboardRepository, times(1)).getProposalOrBugReports(any(), any(), any());
        assertThat(response).isEqualTo(testProposalOrBugReportDashboardResponseWithFalseHasNext);
    }

    @Test
    @DisplayName("lastReportUlid가 null일 때 getProposalOrBug로 건의 및 버그 제보 현황 응답 반환")
    void testGetProposalOrBug_givenNullLastReportUlid_willReturnResponse() {
        // given
        given(reportDashboardRepository.getProposalOrBugReports(any(), any(), any())).willReturn(testProposalOrBugReportDashboardCheckedReadModelList);

        // when
        ProposalOrBugReportDashboardResponse response =
                memberAdminController.getProposalOrBug(new ProposalOrBugReportGetRecord(ProposalOrBugReportStatus.CHECKED, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getProposalOrBugReports(any(), any(), any());
        assertThat(response).isEqualTo(testProposalOrBugReportDashboardResponseWithFalseHasNext);
    }

    @Test
    @DisplayName("조회된 읽기 모델이 비어 있을 때 getProposalOrBug로 건의 및 버그 제보 현황 응답 반환")
    void testGetProposalOrBug_givenEmptyReadModel_willReturnResponse() {
        // given
        given(reportDashboardRepository.getProposalOrBugReports(any(), any(), any())).willReturn(List.of());

        // when
        ProposalOrBugReportDashboardResponse response =
                memberAdminController.getProposalOrBug(new ProposalOrBugReportGetRecord(ProposalOrBugReportStatus.CHECKED, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getProposalOrBugReports(any(), any(), any());
        assertThat(response).isEqualTo(ProposalOrBugReportDashboardResponse.of(List.of(), null, false));
    }

    @Test
    @DisplayName("페이지 크기보다 더 많은 데이터가 있을 때 getProposalOrBug로 hasNext가 true인 응답 반환")
    void testGetProposalOrBug_givenMoreItemsThanPageSize_willReturnResponseWithTrueHasNext() {
        // given
        given(reportDashboardRepository.getProposalOrBugReports(any(), any(), any()))
                .willReturn(List.of(testProposalOrBugReportDashboardCheckedReadModel, testProposalOrBugReportDashboardCheckedReadModel));

        // when
        ProposalOrBugReportDashboardResponse response =
                memberAdminController.getProposalOrBug(new ProposalOrBugReportGetRecord(ProposalOrBugReportStatus.CHECKED, null, 1));

        // then
        verify(reportDashboardRepository, times(1)).getProposalOrBugReports(any(), any(), any());
        assertThat(response).isEqualTo(testProposalOrBugReportDashboardResponseWithTrueHasNext);
    }

    @Test
    @DisplayName("확인되지 않았을 때 checkProposalOrBug로 건의 및 버그 제보 확인")
    void testCheckProposalOrBug_givenNotCheckedRecord_willCheckProposalOrBugReport() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfReportExists(any());
        given(reportRepository.isCheckedInProposalOrBugReport(testReportId)).willReturn(false);
        given(reportDashboardRepository.checkProposalOrBugReport(any())).willReturn(testProposalOrBugReportDashboardCheckedReadModel);

        // when
        ProposalOrBugReportDashboardReadModel readModel = memberAdminController.checkProposalOrBug(testProposalOrBugReportCheckRecord);

        // then
        verify(reportDashboardRepository, times(1)).checkProposalOrBugReport(any());
        assertThat(readModel).isEqualTo(testProposalOrBugReportDashboardCheckedReadModel);
    }

    @Test
    @DisplayName("확인되었을 때 checkProposalOrBug로 건의 및 버그 제보 확인 시 예외 반환")
    void testCheckProposalOrBug_givenCheckedRecord_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfReportExists(any());
        given(reportRepository.isCheckedInProposalOrBugReport(testReportId)).willReturn(true);

        // when
        ExistsValueException existsValueException = assertThrows(
                ExistsValueException.class,
                () -> memberAdminController.checkProposalOrBug(testProposalOrBugReportCheckRecord));

        // then
        assertThat(existsValueException.getErrorCode()).isEqualTo(EXISTS_PROPOSAL_OR_BUG_REPORT_CHECKED);
    }

    @Test
    @DisplayName("보고서가 존재할 때 removeProposalOrBug로 건의 및 버그 제보 제거")
    void testRemoveProposalOrBug_givenValidRecord_willRemoveProposalOrBugReport() {
        // given
        given(reportRepository.isIdExistInProposalOrBugReport(testReportId)).willReturn(true);
        given(reportRepository.isUncheckedInProposalOrBugReport(testReportId)).willReturn(false);
        willDoNothing().given(reportRepository).removeProposalOrBugReport(any());

        // when
        memberAdminController.removeProposalOrBug(testProposalOrBugReportRemoveRecord);

        // then
        verify(reportRepository, times(1)).removeProposalOrBugReport(any());
    }

    @Test
    @DisplayName("보고서가 존재하지 않을 때 removeProposalOrBug로 건의 및 버그 제보 제거")
    void testRemoveProposalOrBug_givenNotFoundReportId_willDoNothing() {
        // given
        given(reportRepository.isIdExistInProposalOrBugReport(testReportId)).willReturn(false);

        // when
        memberAdminController.removeProposalOrBug(testProposalOrBugReportRemoveRecord);

        // then
        verify(reportRepository, never()).removeProposalOrBugReport(any());
    }

    @Test
    @DisplayName("보고서가 존재하고 확인되지 않았을 때 removeProposalOrBug로 건의 및 버그 제보 제거")
    void testRemoveProposalOrBug_givenNotCheckedReportId_willThrowException() {
        // given
        given(reportRepository.isIdExistInProposalOrBugReport(testReportId)).willReturn(true);
        given(reportRepository.isUncheckedInProposalOrBugReport(testReportId)).willReturn(true);

        // when
        ExistsValueException existsValueException = assertThrows(ExistsValueException.class, () -> memberAdminController.removeProposalOrBug(testProposalOrBugReportRemoveRecord));

        // then
        assertThat(existsValueException.getErrorCode()).isEqualTo(NOT_FOUND_PROPOSAL_OR_BUG_REPORT_CHECKED);
    }

    @Test
    @DisplayName("lastPostUlid가 존재하고 유효할 때 getPostAbuseReport로 게시글 신고 현황 응답 반환")
    void testGetPostAbuseReport_givenValidLastPostUlid_willReturnResponse() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(reportDashboardRepository.getPostAbuseReports(any(), any(), any())).willReturn(testPostAbuseReportDashboardReadModelList);

        // when
        PostAbuseReportDashboardResponse response = memberAdminController.getPostAbuseReport(testPostAbuseReportGetRecord);

        // then
        verify(reportDashboardRepository, times(1)).getPostAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(testPostAbuseReportDashboardResponseWithFalseHasNext);
    }

    @Test
    @DisplayName("lastPostUlid가 null일 때 getPostAbuseReport로 게시글 신고 현황 응답 반환")
    void testGetPostAbuseReport_givenNullLastPostUlid_willReturnResponse() {
        // given
        given(reportDashboardRepository.getPostAbuseReports(any(), any(), any())).willReturn(testPostAbuseReportDashboardReadModelList);

        // when
        PostAbuseReportDashboardResponse response =
                memberAdminController.getPostAbuseReport(new PostAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getPostAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(testPostAbuseReportDashboardResponseWithFalseHasNext);
    }

    @Test
    @DisplayName("조회된 읽기 모델이 비어 있을 때 getPostAbuseReport로 게시글 신고 현황 응답 반환")
    void testGetPostAbuseReport_givenEmptyReadModel_willReturnResponse() {
        // given
        given(reportDashboardRepository.getPostAbuseReports(any(), any(), any())).willReturn(List.of());

        // when
        PostAbuseReportDashboardResponse response =
                memberAdminController.getPostAbuseReport(new PostAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getPostAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(PostAbuseReportDashboardResponse.of(List.of(), null, false));
    }

    @Test
    @DisplayName("페이지 크기보다 더 많은 데이터가 있을 때 getPostAbuseReport로 hasNext가 true인 응답 반환")
    void testGetPostAbuseReport_givenMoreItemsThanPageSize_willReturnResponseWithTrueHasNext() {
        // given
        given(reportDashboardRepository.getPostAbuseReports(any(), any(), any()))
                .willReturn(List.of(testPostAbuseReportDashboardReadModel, testPostAbuseReportDashboardReadModel));

        // when
        PostAbuseReportDashboardResponse response =
                memberAdminController.getPostAbuseReport(new PostAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, 1));

        // then
        verify(reportDashboardRepository, times(1)).getPostAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(testPostAbuseReportDashboardResponseWithTrueHasNext);
    }

    @Test
    @DisplayName("lastPostUlid가 존재하지 않을 때 getPostAbuseReport로 예외 반환")
    void testGetPostAbuseReport_givenNotFoundLastPostUlid_willThrowException() {
        // given
        willThrow(new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID, "activitySubjectPostId"))
                .given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> memberAdminController.getPostAbuseReport(testPostAbuseReportGetRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("반려되지 않은 게시글일 때 dismissPostAbuse로 게시글 신고 반려 후 읽기 모델 반환")
    void testDismissPostAbuse_givenNotDismissedPost_willReturnReadModel() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(reportDashboardRepository.isDismissedInPostAbuseReportDashboard(any())).willReturn(false);
        given(reportDashboardRepository.dismissPostAbuseReport(any())).willReturn(testPostAbuseReportDashboardReadModel);

        // when
        PostAbuseReportDashboardReadModel readModel = memberAdminController.dismissPostAbuse(testPostAbuseReportDismissRecord);

        // then
        verify(reportDashboardRepository, times(1)).dismissPostAbuseReport(any());
        assertThat(readModel).isEqualTo(testPostAbuseReportDashboardReadModel);
    }

    @Test
    @DisplayName("게시글을 찾을 수 없을 때 dismissPostAbuse로 예외 반환")
    void testDismissPostAbuse_givenNotFoundPost_willThrowException() {
        // given
        willThrow(new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID, "activitySubjectPostId"))
                .given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> memberAdminController.dismissPostAbuse(testPostAbuseReportDismissRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("이미 반려된 게시글일 때 dismissPostAbuse로 예외 반환")
    void testDismissPostAbuse_givenAlreadyDismissedPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(reportDashboardRepository.isDismissedInPostAbuseReportDashboard(any())).willReturn(true);

        // when
        ExistsValueException existsValueException = assertThrows(
                ExistsValueException.class,
                () -> memberAdminController.dismissPostAbuse(testPostAbuseReportDismissRecord));

        // then
        assertThat(existsValueException.getErrorCode()).isEqualTo(EXISTS_POST_ABUSE_REPORT_DISMISSED);
    }

    @Test
    @DisplayName("수리되지 않은 게시글일 때 approvePostAbuse로 게시글 신고 수리 후 읽기 모델 반환")
    void testApprovePostAbuse_givenNotApprovedPost_willReturnReadModel() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(reportDashboardRepository.isApprovedInPostAbuseReportDashboard(any())).willReturn(false);
        given(reportDashboardRepository.approvePostAbuseReport(any())).willReturn(testPostAbuseReportDashboardReadModel);
        willDoNothing().given(applicationEventPublisher).publishEvent(any(PostAbuseReportApproveEvent.class));

        // when
        PostAbuseReportDashboardReadModel readModel = memberAdminController.approvePostAbuse(testPostAbuseReportApproveRecord);

        // then
        verify(reportDashboardRepository, times(1)).approvePostAbuseReport(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(PostAbuseReportApproveEvent.class));
        assertThat(readModel).isEqualTo(testPostAbuseReportDashboardReadModel);
    }

    @Test
    @DisplayName("게시글을 찾을 수 없을 때 approvePostAbuse로 예외 반환")
    void testApprovePostAbuse_givenNotFoundPost_willThrowException() {
        // given
        willThrow(new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID, "activitySubjectPostId"))
                .given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> memberAdminController.approvePostAbuse(testPostAbuseReportApproveRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("이미 수리된 게시글일 때 approvePostAbuse로 예외 반환")
    void testApprovePostAbuse_givenAlreadyApprovedPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(reportDashboardRepository.isApprovedInPostAbuseReportDashboard(any())).willReturn(true);

        // when
        ExistsValueException existsValueException = assertThrows(
                ExistsValueException.class,
                () -> memberAdminController.approvePostAbuse(testPostAbuseReportApproveRecord));

        // then
        assertThat(existsValueException.getErrorCode()).isEqualTo(EXISTS_POST_ABUSE_REPORT_BLINDED);
        verify(reportDashboardRepository, never()).approvePostAbuseReport(any());
    }

    @Test
    @DisplayName("lastPostUlid와 lastPath 모두 존재하고 유효할 때 getCommentAbuseReport로 댓글 신고 현황 응답 반환")
    void testGetCommentAbuseReport_givenValidLastPostUlidAndLastPath_willReturnResponse() {
        // given
        given(reportDashboardRepository.getCommentAbuseReports(any(), any(), any())).willReturn(testCommentAbuseReportDashboardReadModelList);

        // when
        CommentAbuseReportDashboardResponse response = memberAdminController.getCommentAbuseReport(testCommentAbuseReportGetRecord);

        // then
        verify(reportDashboardRepository, times(1)).getCommentAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(testCommentAbuseReportDashboardResponseWithFalseHasNext);
    }

    @Test
    @DisplayName("lastPostUlid와 lastPath 모두 null일 때 getCommentAbuseReport로 댓글 신고 현황 응답 반환")
    void testGetCommentAbuseReport_givenNullLastPostUlidAndPath_willReturnResponse() {
        // given
        given(reportDashboardRepository.getCommentAbuseReports(any(), any(), any())).willReturn(testCommentAbuseReportDashboardReadModelList);

        // when
        CommentAbuseReportDashboardResponse response =
                memberAdminController.getCommentAbuseReport(new CommentAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getCommentAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(testCommentAbuseReportDashboardResponseWithFalseHasNext);
    }

    @Test
    @DisplayName("조회된 읽기 모델이 비어 있을 때 getCommentAbuseReport로 댓글 신고 현황 응답 반환")
    void testGetCommentAbuseReport_givenEmptyReadModel_willReturnResponse() {
        // given
        given(reportDashboardRepository.getCommentAbuseReports(any(), any(), any())).willReturn(List.of());

        // when
        CommentAbuseReportDashboardResponse response =
                memberAdminController.getCommentAbuseReport(new CommentAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getCommentAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(CommentAbuseReportDashboardResponse.of(List.of(), null, null, false));
    }

    @Test
    @DisplayName("페이지 크기보다 더 많은 데이터가 있을 때 getCommentAbuseReport로 hasNext가 true인 응답 반환")
    void testGetCommentAbuseReport_givenMoreItemsThanPageSize_willReturnResponseWithTrueHasNext() {
        // given
        given(reportDashboardRepository.getCommentAbuseReports(any(), any(), any()))
                .willReturn(List.of(testCommentAbuseReportDashboardReadModel, testCommentAbuseReportDashboardReadModel));

        // when
        CommentAbuseReportDashboardResponse response =
                memberAdminController.getCommentAbuseReport(new CommentAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, null, 1));

        // then
        verify(reportDashboardRepository, times(1)).getCommentAbuseReports(any(), any(), any());
        assertThat(response).isEqualTo(testCommentAbuseReportDashboardResponseWithTrueHasNext);
    }

    @Test
    @DisplayName("lastPostUlid와 lastPath 쌍이 존재하지 않을 때 getCommentAbuseReport로 예외 반환")
    void testGetCommentAbuseReport_givenNotFoundLastPostUlidAndLastPath_willThrowException() {
        // given
        willThrow(new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID, "activitySubjectCommentId"))
                .given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> memberAdminController.getCommentAbuseReport(testCommentAbuseReportGetRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
    }

    @Test
    @DisplayName("반려되지 않은 댓글일 때 dismissCommentAbuse로 댓글 신고 반려 후 읽기 모델 반환")
    void testDismissCommentAbuse_givenNotDismissedComment_willReturnReadModel() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(reportDashboardRepository.isDismissedInCommentAbuseReportDashboard(any())).willReturn(false);
        given(reportDashboardRepository.dismissCommentAbuseReport(any())).willReturn(testCommentAbuseReportDashboardReadModel);

        // when
        CommentAbuseReportDashboardReadModel readModel = memberAdminController.dismissCommentAbuse(testCommentAbuseReportDismissRecord);

        // then
        verify(reportDashboardRepository, times(1)).dismissCommentAbuseReport(any());
        assertThat(readModel).isEqualTo(testCommentAbuseReportDashboardReadModel);
    }

    @Test
    @DisplayName("댓글을 찾을 수 없을 때 dismissCommentAbuse로 예외 반환")
    void testDismissCommentAbuse_givenNotFoundComment_willThrowException() {
        // given
        willThrow(new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID, "activitySubjectCommentId"))
                .given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> memberAdminController.dismissCommentAbuse(testCommentAbuseReportDismissRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
    }

    @Test
    @DisplayName("이미 반려된 댓글일 때 dismissCommentAbuse로 예외 반환")
    void testDismissCommentAbuse_givenAlreadyDismissedComment_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(reportDashboardRepository.isDismissedInCommentAbuseReportDashboard(any())).willReturn(true);

        // when
        ExistsValueException existsValueException = assertThrows(
                ExistsValueException.class,
                () -> memberAdminController.dismissCommentAbuse(testCommentAbuseReportDismissRecord));

        // then
        assertThat(existsValueException.getErrorCode()).isEqualTo(EXISTS_COMMENT_ABUSE_REPORT_DISMISSED);
    }

    @Test
    @DisplayName("수리되지 않은 댓글일 때 approveCommentAbuse로 댓글 신고 수리 후 읽기 모델 반환")
    void testApproveCommentAbuse_givenNotApprovedComment_willReturnReadModel() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(reportDashboardRepository.isApprovedInCommentAbuseReportDashboard(any())).willReturn(false);
        given(reportDashboardRepository.approveCommentAbuseReport(any())).willReturn(testCommentAbuseReportDashboardReadModel);
        willDoNothing().given(applicationEventPublisher).publishEvent(any(CommentAbuseReportApproveEvent.class));

        // when
        CommentAbuseReportDashboardReadModel readModel = memberAdminController.approveCommentAbuse(testCommentAbuseReportApproveRecord);

        // then
        verify(reportDashboardRepository, times(1)).approveCommentAbuseReport(any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(CommentAbuseReportApproveEvent.class));
        assertThat(readModel).isEqualTo(testCommentAbuseReportDashboardReadModel);
    }

    @Test
    @DisplayName("댓글을 찾을 수 없을 때 approveCommentAbuse로 예외 반환")
    void testApproveCommentAbuse_givenNotFoundComment_willThrowException() {
        // given
        willThrow(new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID, "activitySubjectCommentId"))
                .given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> memberAdminController.approveCommentAbuse(testCommentAbuseReportApproveRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
    }

    @Test
    @DisplayName("이미 수리된 댓글일 때 approveCommentAbuse로 예외 반환")
    void testApproveCommentAbuse_givenAlreadyApprovedComment_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(reportDashboardRepository.isApprovedInCommentAbuseReportDashboard(any())).willReturn(true);

        // when
        ExistsValueException existsValueException = assertThrows(
                ExistsValueException.class,
                () -> memberAdminController.approveCommentAbuse(testCommentAbuseReportApproveRecord));

        // then
        assertThat(existsValueException.getErrorCode()).isEqualTo(EXISTS_COMMENT_ABUSE_REPORT_BLINDED);
        verify(reportDashboardRepository, never()).approveCommentAbuseReport(any());
    }
}
