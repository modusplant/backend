package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberAdminController {
    private final ReportRepository reportRepository;

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        if (reportRepository.isIdExist(reportId)) {
            reportRepository.removeProposalOrBugReport(reportId);
        }
    }
}
