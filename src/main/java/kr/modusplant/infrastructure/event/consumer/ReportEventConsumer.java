package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommentAbuseReportEvent;
import kr.modusplant.shared.event.PostAbuseReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.PROP_BUG_REP;
import static kr.modusplant.jooq.Tables.PROP_BUG_REP_ARCHIVE;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.val;

@Component
public class ReportEventConsumer {
    private final DSLContext dsl;
    private final S3FileService s3FileService;

    private final SiteMemberJpaRepository memberJpaRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final CommCommentJpaRepository commentJpaRepository;
    private final PropBugRepJpaRepository propBugRepJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository;

    public ReportEventConsumer(EventBus eventBus, DSLContext dsl, S3FileService s3FileService,
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
                        proposalOrBugReportEvent.getReportId(),
                        proposalOrBugReportEvent.getTitle(),
                        proposalOrBugReportEvent.getContent(),
                        proposalOrBugReportEvent.getImagePath());
            } else if (event instanceof ProposalOrBugReportRemoveEvent proposalOrBugReportRemoveEvent) {
                deleteProposalOrBugReport(
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
        this.s3FileService = s3FileService;
        this.memberJpaRepository = memberJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
        this.propBugRepJpaRepository = propBugRepJpaRepository;
        this.postAbuRepJpaRepository = postAbuRepJpaRepository;
        this.commentAbuRepJpaRepository = commentAbuRepJpaRepository;
    }

    private void addProposalOrBugReport(UUID memberId, String reportId, String title, String content, String imagePath) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        propBugRepJpaRepository.save(
                PropBugRepEntity.builder()
                        .ulid(reportId)
                        .member(memberEntity)
                        .title(title)
                        .content(content)
                        .imagePath(imagePath)
                        .build());
    }

    private void deleteProposalOrBugReport(String reportId) {
        deleteImageFromReportImagePath(reportId);
        processProposalOrBugReportRelatedRecords(reportId);
    }

    private void deleteImageFromReportImagePath(String reportId) {
        String imagePath =
                dsl.select(PROP_BUG_REP.IMAGE_PATH)
                        .from(PROP_BUG_REP)
                        .where(PROP_BUG_REP.ULID.eq(reportId))
                        .fetchSingle().into(String.class);

        if (!StringUtils.isBlank(imagePath)) {
            s3FileService.deleteFiles(imagePath);
        }
    }

    private void processProposalOrBugReportRelatedRecords(String reportId) {
        dsl.insertInto(PROP_BUG_REP_ARCHIVE,
                        PROP_BUG_REP_ARCHIVE.ULID,
                        PROP_BUG_REP_ARCHIVE.MEMB_UUID,
                        PROP_BUG_REP_ARCHIVE.TITLE,
                        PROP_BUG_REP_ARCHIVE.CONTENT,
                        PROP_BUG_REP_ARCHIVE.CREATED_AT,
                        PROP_BUG_REP_ARCHIVE.ARCHIVED_AT,
                        PROP_BUG_REP_ARCHIVE.LAST_MODIFIED_AT
                )
                .select(
                        select(
                                PROP_BUG_REP.ULID,
                                PROP_BUG_REP.MEMB_UUID,
                                PROP_BUG_REP.TITLE,
                                PROP_BUG_REP.CONTENT,
                                PROP_BUG_REP.CREATED_AT,
                                val(LocalDateTime.now()),
                                PROP_BUG_REP.LAST_MODIFIED_AT
                        )
                                .from(PROP_BUG_REP)
                                .where(PROP_BUG_REP.ULID.eq(reportId))
                ).execute();

        dsl.deleteFrom(PROP_BUG_REP).where(PROP_BUG_REP.ULID.eq(reportId)).execute();
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
