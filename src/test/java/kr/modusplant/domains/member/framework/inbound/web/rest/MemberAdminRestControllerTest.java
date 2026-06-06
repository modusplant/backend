package kr.modusplant.domains.member.framework.inbound.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberAdminController;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.response.CommentAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.PostAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportDashboardResponse;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;
import static kr.modusplant.domains.member.common.util.usecase.model.read.CommentAbuseReportDashboardReadModelTestUtils.testCommentAbuseReportDashboardReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModel;
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
import static kr.modusplant.domains.member.common.util.usecase.response.PostAbuseDashboardResponseTestUtils.testPostAbuseReportDashboardResponseWithFalseHasNext;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportDashboardResponseTestUtils.testProposalOrBugReportDashboardResponseWithFalseHasNext;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class MemberAdminRestControllerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final MemberAdminController memberAdminController = Mockito.mock(MemberAdminController.class);
    private final MemberAdminRestController memberAdminRestController = new MemberAdminRestController(memberAdminController);

    @Test
    @DisplayName("getProposalOrBugReport로 응답 반환")
    void testGetProposalOrBugReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.getProposalOrBug(testProposalOrBugReportGetRecord)).willReturn(testProposalOrBugReportDashboardResponseWithFalseHasNext);

        // when
        ResponseEntity<DataResponse<ProposalOrBugReportDashboardResponse>> responseEntity = memberAdminRestController.getProposalOrBugReport(ProposalOrBugReportStatus.CHECKED, TEST_REPORT_ULID, TEST_REPORT_SIZE);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testProposalOrBugReportDashboardResponseWithFalseHasNext).toString());
    }

    @Test
    @DisplayName("checkProposalOrBugReport로 응답 반환")
    void testCheckProposalOrBugReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.checkProposalOrBug(testProposalOrBugReportCheckRecord)).willReturn(testProposalOrBugReportDashboardCheckedReadModel);

        // when
        ResponseEntity<DataResponse<ProposalOrBugReportDashboardReadModel>> responseEntity = memberAdminRestController.checkProposalOrBugReport(TEST_REPORT_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testProposalOrBugReportDashboardCheckedReadModel).toString());
    }

    @Test
    @DisplayName("deleteProposalOrBugReport로 응답 반환")
    void testDeleteProposalOrBugReport_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberAdminController).removeProposalOrBug(testProposalOrBugReportRemoveRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberAdminRestController.removeProposalOrBugReport(TEST_REPORT_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("getPostAbuseReport로 응답 반환")
    void testGetPostAbuseReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.getPostAbuseReport(testPostAbuseReportGetRecord)).willReturn(testPostAbuseReportDashboardResponseWithFalseHasNext);

        // when
        ResponseEntity<DataResponse<PostAbuseReportDashboardResponse>> responseEntity =
                memberAdminRestController.getPostAbuseReport(AbuseReportStatus.UNCHECKED, TEST_POST_ULID, TEST_REPORT_SIZE);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testPostAbuseReportDashboardResponseWithFalseHasNext).toString());
    }

    @Test
    @DisplayName("dismissPostAbuseReport로 응답 반환")
    void testDismissPostAbuseReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.dismissPostAbuse(testPostAbuseReportDismissRecord)).willReturn(testPostAbuseReportDashboardReadModel);

        // when
        ResponseEntity<DataResponse<PostAbuseReportDashboardReadModel>> responseEntity =
                memberAdminRestController.dismissPostAbuseReport(TEST_POST_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testPostAbuseReportDashboardReadModel).toString());
    }

    @Test
    @DisplayName("approvePostAbuseReport로 응답 반환")
    void testApprovePostAbuseReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.approvePostAbuse(testPostAbuseReportApproveRecord))
                .willReturn(testPostAbuseReportDashboardReadModel);

        // when
        ResponseEntity<DataResponse<PostAbuseReportDashboardReadModel>> responseEntity =
                memberAdminRestController.approvePostAbuseReport(TEST_POST_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString())
                .isEqualTo(DataResponse.ok(testPostAbuseReportDashboardReadModel).toString());
    }

    @Test
    @DisplayName("getCommentAbuseReport로 응답 반환")
    void testGetCommentAbuseReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.getCommentAbuseReport(testCommentAbuseReportGetRecord))
                .willReturn(testCommentAbuseReportDashboardResponseWithFalseHasNext);

        // when
        ResponseEntity<DataResponse<CommentAbuseReportDashboardResponse>> responseEntity =
                memberAdminRestController.getCommentAbuseReport(AbuseReportStatus.UNCHECKED, TEST_POST_ULID, TEST_COMMENT_PATH, TEST_REPORT_SIZE);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString())
                .isEqualTo(DataResponse.ok(testCommentAbuseReportDashboardResponseWithFalseHasNext).toString());
    }

    @Test
    @DisplayName("dismissCommentAbuseReport로 응답 반환")
    void testDismissCommentAbuseReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.dismissCommentAbuse(testCommentAbuseReportDismissRecord))
                .willReturn(testCommentAbuseReportDashboardReadModel);

        // when
        ResponseEntity<DataResponse<CommentAbuseReportDashboardReadModel>> responseEntity =
                memberAdminRestController.dismissCommentAbuseReport(TEST_POST_ULID, TEST_COMMENT_PATH);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString())
                .isEqualTo(DataResponse.ok(testCommentAbuseReportDashboardReadModel).toString());
    }

    @Test
    @DisplayName("approveCommentAbuseReport로 응답 반환")
    void testApproveCommentAbuseReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.approveCommentAbuse(testCommentAbuseReportApproveRecord))
                .willReturn(testCommentAbuseReportDashboardReadModel);

        // when
        ResponseEntity<DataResponse<CommentAbuseReportDashboardReadModel>> responseEntity =
                memberAdminRestController.approveCommentAbuseReport(TEST_POST_ULID, TEST_COMMENT_PATH);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString())
                .isEqualTo(DataResponse.ok(testCommentAbuseReportDashboardReadModel).toString());
    }
}