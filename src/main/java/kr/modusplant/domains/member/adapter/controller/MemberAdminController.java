package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.event.PostAbuseReportEvent;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;
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

    public List<ProposalOrBugReportAdminPageReadModel> getProposalOrBug(ProposalOrBugReportGetRecord record) {
        if (record.lastReportUlid() != null) {
            ReportId reportId = ReportId.create(record.lastReportUlid());
            memberValidationHelper.validateIfReportExists(reportId);
            return reportRepository.getProposalOrBugReports(ReportPageSize.create(record.size()), record.status(), reportId);
        } else {
            return reportRepository.getProposalOrBugReports(ReportPageSize.create(record.size()), record.status(), null);
        }
    }

    public ProposalOrBugReportAdminPageReadModel checkProposalOrBug(ProposalOrBugReportCheckRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        memberValidationHelper.validateIfReportExists(reportId);
        if (reportRepository.isCheckedInProposalOrBugReport(reportId)) {
            throw new ExistsValueException(EXISTS_REPORT_CHECKED_AT, "checkedAt");
        }
        return reportRepository.checkProposalOrBugReport(reportId);
    }

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        if (reportRepository.isIdExistInProposalOrBugReport(reportId)) {
            reportRepository.removeProposalOrBugReport(reportId);
        }
    }

    public void upsertPostAbuseReportDashboard(PostAbuseReportEvent event) {
        ActivitySubjectPostId postId = ActivitySubjectPostId.create(event.getPostUlid());
        ReportTime reportTime = ReportTime.create(event.getCreatedAt());
        memberValidationHelper.validateIfActivitySubjectPostExists(postId);

        reportDashboardRepository.addPostAbuseReport(postId, reportTime);
    }
}
