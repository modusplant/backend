package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.ProposalBugReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.record.FilenameAndSrcEntityRecord;
import kr.modusplant.domains.member.framework.out.jpa.repository.CommentAbuseReportJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.PostAbuseReportJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.ProposalBugReportJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.event.CommentAbuseReportEvent;
import kr.modusplant.shared.event.PostAbuseReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportEvent;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import kr.modusplant.shared.persistence.compositekey.CommentCompositeKey;
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
    private final AmazonS3Service amazonS3Service;
    private final MemberJpaRepository memberJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final ProposalBugReportJpaRepository proposalBugReportJpaRepository;
    private final PostAbuseReportJpaRepository postAbuRepJpaRepository;
    private final CommentAbuseReportJpaRepository commentAbuseReportJpaRepository;

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
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        proposalBugReportJpaRepository.save(
                ProposalBugReportEntity.builder()
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

        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        PostEntity postEntity = postJpaRepository.findByUlid(postUlid).orElseThrow();
        postAbuRepJpaRepository.save(PostAbuseReportEntity.builder().member(memberEntity).post(postEntity).build());
    }

    @EventListener
    public void handleCommentAbuseReportEvent(CommentAbuseReportEvent commentAbuseReportEvent) {
        UUID memberId = commentAbuseReportEvent.getMemberId();
        String postUlid = commentAbuseReportEvent.getPostUlid();
        String path = commentAbuseReportEvent.getPath();

        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId).orElseThrow();
        CommentEntity commentEntity = commentJpaRepository.findById(
                CommentCompositeKey.builder().post(postUlid).path(path).build()).orElseThrow();
        commentAbuseReportJpaRepository.save(
                CommentAbuseReportEntity.builder().member(memberEntity).comment(commentEntity).build());
    }

    private void deleteImageFromReportImagePath(String reportId) {
        List<String> srcList = dsl.select(
                        field("jsonb_array_elements({0}) ->> 'src'", String.class, PROP_BUG_REP.IMAGE)
                )
                .from(PROP_BUG_REP)
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchInto(String.class);

        if (!srcList.isEmpty()) {
            amazonS3Service.deleteFiles(srcList);
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
