package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportCheckRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportGetRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;
import kr.modusplant.shared.exception.ExistsValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EXISTS_REPORT_CHECKED_AT;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberAdminController {
    private final MemberValidationHelper memberValidationHelper;
    private final ReportRepository reportRepository;
    private final ReportDashboardRepository reportDashboardRepository;

    public List<ProposalOrBugReportDashboardReadModel> getProposalOrBug(ProposalOrBugReportGetRecord record) {
        if (record.lastReportUlid() != null) {
            ReportId reportId = ReportId.create(record.lastReportUlid());
            memberValidationHelper.validateIfReportExists(reportId);
            return reportDashboardRepository.getProposalOrBugReports(ReportPageSize.create(record.size()), record.status(), reportId);
        } else {
            return reportDashboardRepository.getProposalOrBugReports(ReportPageSize.create(record.size()), record.status(), null);
        }
    }

    public ProposalOrBugReportDashboardReadModel checkProposalOrBug(ProposalOrBugReportCheckRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        memberValidationHelper.validateIfReportExists(reportId);
        if (reportRepository.isCheckedInProposalOrBugReport(reportId)) {
            throw new ExistsValueException(EXISTS_REPORT_CHECKED_AT, "checkedAt");
        }
        return reportDashboardRepository.checkProposalOrBugReport(reportId);
    }

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        if (reportRepository.isIdExistInProposalOrBugReport(reportId)) {
            reportRepository.removeProposalOrBugReport(reportId);
        }
    }
}
