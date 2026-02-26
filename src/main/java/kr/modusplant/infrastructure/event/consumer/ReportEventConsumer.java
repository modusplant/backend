package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.jpa.entity.CommPostAbuRepEntity;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.PropBugRepEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.CommPostAbuRepJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.PropBugRepJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.PostAbuseReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportEventConsumer {
    private final SiteMemberJpaRepository memberJpaRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final PropBugRepJpaRepository propBugRepJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;

    public ReportEventConsumer(EventBus eventBus, SiteMemberJpaRepository memberJpaRepository, CommPostJpaRepository postJpaRepository, PropBugRepJpaRepository propBugRepJpaRepository, CommPostAbuRepJpaRepository postAbuRepJpaRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof ProposalOrBugReportEvent proposalOrBugReportEvent) {
                addProposalOrBugReport(proposalOrBugReportEvent.getMemberId(), proposalOrBugReportEvent.getTitle(), proposalOrBugReportEvent.getContent(), proposalOrBugReportEvent.getImagePath());
            }
            if (event instanceof PostAbuseReportEvent postAbuseReportEvent) {
                addPostAbuseReport(postAbuseReportEvent.getMemberId(), postAbuseReportEvent.getPostUlid());
            }
        });
        this.memberJpaRepository = memberJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.propBugRepJpaRepository = propBugRepJpaRepository;
        this.postAbuRepJpaRepository = postAbuRepJpaRepository;
    }

    private void addProposalOrBugReport(UUID memberId, String title, String content, String imagePath) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        propBugRepJpaRepository.save(PropBugRepEntity.builder().member(memberEntity).title(title).content(content).imagePath(imagePath).build());
    }

    private void addPostAbuseReport(UUID memberId, String postUlid) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommPostEntity postEntity = postJpaRepository.findByUlid(postUlid).orElseThrow();
        postAbuRepJpaRepository.save(CommPostAbuRepEntity.builder().member(memberEntity).post(postEntity).build());
    }
}
