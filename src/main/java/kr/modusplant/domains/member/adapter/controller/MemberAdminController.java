package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberAdminController {
    private final MemberValidationHelper memberValidationHelper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        memberValidationHelper.validateIfReportExists(reportId);

        applicationEventPublisher.publishEvent(
                ProposalOrBugReportRemoveEvent.create(reportId.getValue()));
    }
}
