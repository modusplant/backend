package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.entity.record.FilenameAndSrcEntityRecord;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.shared.event.CommentAbuseReportEvent;
import kr.modusplant.shared.event.PostAbuseReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.PROP_BUG_REP;
import static kr.modusplant.jooq.Tables.PROP_BUG_REP_ARCHIVE;
import static org.jooq.impl.DSL.*;

@Component
@RequiredArgsConstructor
public class ReportEventListener {
    private final DSLContext dsl;
    private final S3FileService s3FileService;
    private final SiteMemberJpaRepository memberJpaRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final CommCommentJpaRepository commentJpaRepository;
    private final PropBugRepJpaRepository propBugRepJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository;

    @EventListener
    public void handleProposalOrBugReportEvent(ProposalOrBugReportEvent proposalOrBugReportEvent) {
        UUID memberId = proposalOrBugReportEvent.getMemberId();
        String reportId = proposalOrBugReportEvent.getReportId();
        String title = proposalOrBugReportEvent.getTitle();
        String content = proposalOrBugReportEvent.getContent();
        List<String> filenames = proposalOrBugReportEvent.getFilenames();
        List<String> imagePaths = proposalOrBugReportEvent.getImagePaths();

        List<FilenameAndSrcEntityRecord> imageList = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            imageList.add(new FilenameAndSrcEntityRecord(filenames.get(i), imagePaths.get(i)));
        }
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        propBugRepJpaRepository.save(
                PropBugRepEntity.builder()
                        .ulid(reportId)
                        .member(memberEntity)
                        .title(title)
                        .content(content)
                        .image(imageList)
                        .build());
    }

    @EventListener
    public void handleProposalOrBugReportRemoveEvent(ProposalOrBugReportRemoveEvent proposalOrBugReportRemoveEvent) {
        String reportId = proposalOrBugReportRemoveEvent.getReportId();

        deleteImageFromReportImagePath(reportId);
        processProposalOrBugReportRelatedRecords(reportId);
    }

    @EventListener
    public void handlePostAbuseReportEvent(PostAbuseReportEvent postAbuseReportEvent) {
        UUID memberId = postAbuseReportEvent.getMemberId();
        String postUlid = postAbuseReportEvent.getPostUlid();

        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommPostEntity postEntity = postJpaRepository.findByUlid(postUlid).orElseThrow();
        postAbuRepJpaRepository.save(CommPostAbuRepEntity.builder().member(memberEntity).post(postEntity).build());
    }

    @EventListener
    public void handleCommentAbuseReportEvent(CommentAbuseReportEvent commentAbuseReportEvent) {
        UUID memberId = commentAbuseReportEvent.getMemberId();
        String postUlid = commentAbuseReportEvent.getPostUlid();
        String path = commentAbuseReportEvent.getPath();

        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommCommentEntity commentEntity = commentJpaRepository.findById(
                CommCommentId.builder().post(postUlid).path(path).build()).orElseThrow();
        commentAbuRepJpaRepository.save(
                CommCommentAbuRepEntity.builder().member(memberEntity).comment(commentEntity).build());
    }

    private void deleteImageFromReportImagePath(String reportId) {
        List<String> srcList = dsl.select(
                        field("jsonb_array_elements({0}) ->> 'src'", String.class, PROP_BUG_REP.IMAGE)
                )
                .from(PROP_BUG_REP)
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchInto(String.class);

        if (!srcList.isEmpty()) {
            s3FileService.deleteFiles(srcList);
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
}
