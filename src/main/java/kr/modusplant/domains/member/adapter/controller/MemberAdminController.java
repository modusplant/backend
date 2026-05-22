package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportCheckRecord;
import kr.modusplant.shared.exception.ExistsValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EXISTS_REPORT_CHECKED_AT;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberAdminController {
    private final MemberValidationHelper memberValidationHelper;
    private final ReportRepository reportRepository;

    public void checkProposalOrBug(ProposalOrBugReportCheckRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        memberValidationHelper.validateIfReportExists(reportId);
        if (reportRepository.isCheckedInProposalOrBugReport(reportId)) {
            throw new ExistsValueException(EXISTS_REPORT_CHECKED_AT, "checkedAt");
        }
        reportRepository.checkProposalOrBugReport(reportId);
    }

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        if (reportRepository.isIdExistInProposalOrBugReport(reportId)) {
            reportRepository.removeProposalOrBugReport(reportId);
        }
    }
}
