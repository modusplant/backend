package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.shared.exception.ExistsValueException;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportCheckRecordTestUtils.testProposalOrBugReportCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRemoveRecordTestUtils.testProposalOrBugReportRemoveRecord;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EXISTS_REPORT_CHECKED_AT;
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

    private final MemberAdminController memberAdminController = new MemberAdminController(memberValidationHelper, reportRepository);

    @Test
    @DisplayName("확인되지 않았을 때 checkProposalOrBug로 건의 및 버그 제보 확인")
    void testCheckProposalOrBug_givenNotCheckedRecord_willCheckProposalOrBugReport() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfReportExists(any());
        given(reportRepository.isCheckedInProposalOrBugReport(testReportId)).willReturn(false);
        willDoNothing().given(reportRepository).checkProposalOrBugReport(any());

        // when
        memberAdminController.checkProposalOrBug(testProposalOrBugReportCheckRecord);

        // then
        verify(reportRepository, times(1)).checkProposalOrBugReport(any());
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
}