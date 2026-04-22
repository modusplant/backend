package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommentAbuseReportEvent;
import kr.modusplant.shared.event.PostAbuseReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static kr.modusplant.jooq.Tables.PROP_BUG_REP_ARCHIVE;

@Component
public class ReportEventConsumer {
    private final DSLContext dsl;

    private final SiteMemberJpaRepository memberJpaRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final CommCommentJpaRepository commentJpaRepository;
    private final PropBugRepJpaRepository propBugRepJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository;

    public ReportEventConsumer(EventBus eventBus, DSLContext dsl,
                               SiteMemberJpaRepository memberJpaRepository,
                               CommPostJpaRepository postJpaRepository,
                               CommCommentJpaRepository commentJpaRepository,
                               PropBugRepJpaRepository propBugRepJpaRepository,
                               CommPostAbuRepJpaRepository postAbuRepJpaRepository,
                               CommCommentAbuRepJpaRepository commentAbuRepJpaRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof ProposalOrBugReportEvent proposalOrBugReportEvent) {
                addProposalOrBugReport(
                        proposalOrBugReportEvent.getMemberId(),
                        proposalOrBugReportEvent.getTitle(),
                        proposalOrBugReportEvent.getContent(),
                        proposalOrBugReportEvent.getImagePath());
            } else if (event instanceof ProposalOrBugReportRemoveEvent proposalOrBugReportRemoveEvent) {
                deleteProposalOrBugReport(
                        proposalOrBugReportRemoveEvent.getMemberId(),
                        proposalOrBugReportRemoveEvent.getReportId()
                );
            } else if (event instanceof PostAbuseReportEvent postAbuseReportEvent) {
                addPostAbuseReport(postAbuseReportEvent.getMemberId(), postAbuseReportEvent.getPostUlid());
            }
            else if (event instanceof CommentAbuseReportEvent commentAbuseReportEvent) {
                addCommentAbuseReport(
                        commentAbuseReportEvent.getMemberId(),
                        commentAbuseReportEvent.getPostUlid(),
                        commentAbuseReportEvent.getPath());
            }
        });
        this.dsl = dsl;
        this.memberJpaRepository = memberJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
        this.propBugRepJpaRepository = propBugRepJpaRepository;
        this.postAbuRepJpaRepository = postAbuRepJpaRepository;
        this.commentAbuRepJpaRepository = commentAbuRepJpaRepository;
    }

    private void addProposalOrBugReport(UUID memberId, String title, String content, String imagePath) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        propBugRepJpaRepository.save(
                PropBugRepEntity.builder()
                        .member(memberEntity)
                        .title(title)
                        .content(content)
                        .imagePath(imagePath)
                        .build());
    }

    private void deleteProposalOrBugReport(UUID memberId, String reportId) {
//        dsl.insertInto(PROP_BUG_REP_ARCHIVE,
//                        COMM_POST_ARCHIVE.ULID,
//                        COMM_POST_ARCHIVE.PRI_CATE_ID,
//                        COMM_POST_ARCHIVE.SECO_CATE_ID,
//                        COMM_POST_ARCHIVE.AUTH_MEMB_UUID,
//                        COMM_POST_ARCHIVE.TITLE,
//                        COMM_POST_ARCHIVE.CONTENT_TEXT,
//                        COMM_POST_ARCHIVE.CREATED_AT,
//                        COMM_POST_ARCHIVE.ARCHIVED_AT,
//                        COMM_POST_ARCHIVE.UPDATED_AT,
//                        COMM_POST_ARCHIVE.PUBLISHED_AT
//                )
//                .select(
//                        select(
//                                COMM_POST.ULID,
//                                COMM_POST.PRI_CATE_ID,
//                                COMM_POST.SECO_CATE_ID,
//                                COMM_POST.AUTH_MEMB_UUID,
//                                COMM_POST.TITLE,
//                                COMM_POST.CONTENT_TEXT,
//                                COMM_POST.CREATED_AT,
//                                val(LocalDateTime.now()),
//                                COMM_POST.UPDATED_AT,
//                                COMM_POST.PUBLISHED_AT
//                        )
//                                .from(COMM_POST)
//                                .where(COMM_POST.ULID.in(publishedPostUlids))
//                )
    }

    private void addPostAbuseReport(UUID memberId, String postUlid) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommPostEntity postEntity = postJpaRepository.findByUlid(postUlid).orElseThrow();
        postAbuRepJpaRepository.save(CommPostAbuRepEntity.builder().member(memberEntity).post(postEntity).build());
    }

    private void addCommentAbuseReport(UUID memberId, String postUlid, String path) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommCommentEntity commentEntity = commentJpaRepository.findById(
                CommCommentId.builder().post(postUlid).path(path).build()).orElseThrow();
        commentAbuRepJpaRepository.save(
                CommCommentAbuRepEntity.builder().member(memberEntity).comment(commentEntity).build());
    }
}
