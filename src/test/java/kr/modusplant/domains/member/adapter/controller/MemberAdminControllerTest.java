package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.PostAbuseReportGetRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportGetRecord;
import kr.modusplant.shared.exception.ExistsValueException;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportGetRecordTestUtils.testPostAbuseReportGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportCheckRecordTestUtils.testProposalOrBugReportCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordGetTestUtils.testProposalOrBugReportGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRemoveRecordTestUtils.testProposalOrBugReportRemoveRecord;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EXISTS_REPORT_CHECKED_AT;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_ACTIVITY_SUBJECT_POST_ID;
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

    private final MemberAdminController memberAdminController = new MemberAdminController(memberValidationHelper, reportRepository, reportDashboardRepository);

    @Test
    @DisplayName("lastReportUlid가 null이 아닐 때 getProposalOrBug로 건의 및 버그 제보 조회")
    void testGetProposalOrBug_givenValidLastReportUlid_willGetProposalOrBugReport() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfReportExists(any());
        given(reportDashboardRepository.getProposalOrBugReports(any(), any(), any())).willReturn(testProposalOrBugReportDashboardCheckedReadModelList);

        // when
        List<ProposalOrBugReportDashboardReadModel> readModels = memberAdminController.getProposalOrBug(testProposalOrBugReportGetRecord);

        // then
        verify(reportDashboardRepository, times(1)).getProposalOrBugReports(any(), any(), any());
        assertThat(readModels).isEqualTo(testProposalOrBugReportDashboardCheckedReadModelList);
    }

    @Test
    @DisplayName("lastReportUlid가 null일 때 getProposalOrBug로 건의 및 버그 제보 조회")
    void testGetProposalOrBug_givenNullLastReportUlid_willGetProposalOrBugReport() {
        // given
        given(reportDashboardRepository.getProposalOrBugReports(any(), any(), any())).willReturn(testProposalOrBugReportDashboardCheckedReadModelList);

        // when
        List<ProposalOrBugReportDashboardReadModel> readModels =
                memberAdminController.getProposalOrBug(new ProposalOrBugReportGetRecord(ProposalOrBugReportStatus.CHECKED, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getProposalOrBugReports(any(), any(), any());
        assertThat(readModels).isEqualTo(testProposalOrBugReportDashboardCheckedReadModelList);
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
        assertThat(existsValueException.getErrorCode()).isEqualTo(EXISTS_REPORT_CHECKED_AT);
    }

    @Test
    @DisplayName("보고서가 존재할 때 removeProposalOrBug로 건의 및 버그 제보 제거")
    void testRemoveProposalOrBug_givenValidRecord_willRemoveProposalOrBugReport() {
        // given
        given(reportRepository.isIdExistInProposalOrBugReport(testReportId)).willReturn(true);
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
    @DisplayName("lastPostUlid가 존재하고 유효할 때 getPostAbuseReport로 게시글 신고 현황 목록 반환")
    void testGetPostAbuseReport_givenValidLastPostUlid_willReturnReadModelList() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(reportDashboardRepository.getPostAbuseReports(any(), any(), any())).willReturn(testPostAbuseReportDashboardReadModelList);

        // when
        List<PostAbuseReportDashboardReadModel> readModels = memberAdminController.getPostAbuseReport(testPostAbuseReportGetRecord);

        // then
        verify(reportDashboardRepository, times(1)).getPostAbuseReports(any(), any(), any());
        assertThat(readModels).isEqualTo(testPostAbuseReportDashboardReadModelList);
    }

    @Test
    @DisplayName("lastPostUlid가 null일 때 getPostAbuseReport로 게시글 신고 현황 목록 반환")
    void testGetPostAbuseReport_givenNullLastPostUlid_willReturnReadModelList() {
        // given
        given(reportDashboardRepository.getPostAbuseReports(any(), any(), any())).willReturn(testPostAbuseReportDashboardReadModelList);

        // when
        List<PostAbuseReportDashboardReadModel> readModels =
                memberAdminController.getPostAbuseReport(new PostAbuseReportGetRecord(AbuseReportStatus.UNCHECKED, null, TEST_REPORT_SIZE));

        // then
        verify(reportDashboardRepository, times(1)).getPostAbuseReports(any(), any(), any());
        assertThat(readModels).isEqualTo(testPostAbuseReportDashboardReadModelList);
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
}