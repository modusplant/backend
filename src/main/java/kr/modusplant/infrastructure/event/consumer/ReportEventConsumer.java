package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.jpa.entity.PropBugRepEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.PropBugRepJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportEventConsumer {
    private final SiteMemberJpaRepository memberJpaRepository;
    private final PropBugRepJpaRepository propBugRepJpaRepository;

    public ReportEventConsumer(EventBus eventBus, SiteMemberJpaRepository memberJpaRepository, PropBugRepJpaRepository propBugRepJpaRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof ProposalOrBugReportEvent proposalOrBugReportEvent) {
                addProposalOrBugReport(proposalOrBugReportEvent.getMemberId(), proposalOrBugReportEvent.getTitle(), proposalOrBugReportEvent.getContent(), proposalOrBugReportEvent.getImagePath());
            }
        });
        this.memberJpaRepository = memberJpaRepository;
        this.propBugRepJpaRepository = propBugRepJpaRepository;
    }

    private void addProposalOrBugReport(UUID memberId, String title, String content, String imagePath) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        propBugRepJpaRepository.save(PropBugRepEntity.builder().member(memberEntity).title(title).content(content).imagePath(imagePath).build());
    }
}
