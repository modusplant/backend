package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommentAbuseReportEvent;
import kr.modusplant.shared.event.PostAbuseReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportEventConsumer {
    private final SiteMemberJpaRepository memberJpaRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final CommCommentJpaRepository commentJpaRepository;
    private final PropBugRepJpaRepository propBugRepJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository;

    public ReportEventConsumer(EventBus eventBus, SiteMemberJpaRepository memberJpaRepository, CommPostJpaRepository postJpaRepository, CommCommentJpaRepository commentJpaRepository, PropBugRepJpaRepository propBugRepJpaRepository, CommPostAbuRepJpaRepository postAbuRepJpaRepository, CommCommentAbuRepJpaRepository commentAbuRepJpaRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof ProposalOrBugReportEvent proposalOrBugReportEvent) {
                addProposalOrBugReport(proposalOrBugReportEvent.getMemberId(), proposalOrBugReportEvent.getTitle(), proposalOrBugReportEvent.getContent(), proposalOrBugReportEvent.getImagePath());
            }
            else if (event instanceof PostAbuseReportEvent postAbuseReportEvent) {
                addPostAbuseReport(postAbuseReportEvent.getMemberId(), postAbuseReportEvent.getPostUlid());
            }
            else if (event instanceof CommentAbuseReportEvent commentAbuseReportEvent) {
                addCommentAbuseReport(commentAbuseReportEvent.getMemberId(), commentAbuseReportEvent.getPostUlid(), commentAbuseReportEvent.getPath());
            }
        });
        this.memberJpaRepository = memberJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
        this.propBugRepJpaRepository = propBugRepJpaRepository;
        this.postAbuRepJpaRepository = postAbuRepJpaRepository;
        this.commentAbuRepJpaRepository = commentAbuRepJpaRepository;
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

    private void addCommentAbuseReport(UUID memberId, String postUlid, String path) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommCommentEntity commentEntity = commentJpaRepository.findById(CommCommentId.builder().postUlid(postUlid).path(path).build()).orElseThrow();
        commentAbuRepJpaRepository.save(CommCommentAbuRepEntity.builder().member(memberEntity).comment(commentEntity).build());
    }
}
