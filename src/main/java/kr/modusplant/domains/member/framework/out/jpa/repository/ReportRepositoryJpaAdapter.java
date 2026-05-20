package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.ProposalBugReportEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.record.FilenameAndSrcEntityRecord;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.event.ImageRemoveEvent;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.modusplant.jooq.Tables.PROP_BUG_REP;
import static kr.modusplant.jooq.Tables.PROP_BUG_REP_ARCHIVE;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryJpaAdapter implements ReportRepository {
    private final DSLContext dsl;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final MemberJpaRepository memberJpaRepository;
    private final ProposalBugReportJpaRepository proposalBugReportJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final PostAbuseReportJpaRepository postAbuRepJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentAbuseReportJpaRepository commentAbuseReportJpaRepository;

    @Override
    public boolean isIdExist(ReportId reportId) {
        return proposalBugReportJpaRepository.existsByUlid(reportId.getValue());
    }

    @Override
    public boolean isMemberAbusePost(MemberId memberId, TargetPostId targetPostId) {
        return postAbuRepJpaRepository.findByMemberIdAndPost(
                memberId.getValue(),
                postJpaRepository.findByUlid(targetPostId.getValue()).orElseThrow()
        ).isPresent();
    }

    @Override
    public boolean isMemberAbuseComment(MemberId memberId, TargetCommentId targetCommentId) {
        return commentAbuseReportJpaRepository.findByMemberIdAndComment(
                memberId.getValue(),
                commentJpaRepository.findByPostUlidAndPath(
                        targetCommentId.getTargetPostId().getValue(),
                        targetCommentId.getTargetCommentPath().getValue()).orElseThrow()
        ).isPresent();
    }

    @Override
    public void reportProposalOrBug(MemberId memberId, ProposalOrBugReport proposalOrBugReport) {
        String reportId = proposalOrBugReport.getReportId().getValue();
        String title = proposalOrBugReport.getReportTitle().getValue();
        String content = proposalOrBugReport.getReportContent().getValue();
        List<String> filenames =
                proposalOrBugReport.getProposalOrBugReportImages()
                        .stream()
                        .map(element -> element.getProposalOrBugReportImageFileName().getFileName())
                        .toList();
        List<String> imagePaths =
                proposalOrBugReport.getProposalOrBugReportImages()
                        .stream()
                        .map(element -> element.getReportImagePath().getValue())
                        .toList();

        List<FilenameAndSrcEntityRecord> imageList = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            imageList.add(new FilenameAndSrcEntityRecord(filenames.get(i), imagePaths.get(i)));
        }
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue()).orElseThrow();
        proposalBugReportJpaRepository.save(
                ProposalBugReportEntity.builder()
                        .ulid(reportId)
                        .member(memberEntity)
                        .title(title)
                        .content(content)
                        .image(imageList)
                        .build());
    }

    @Override
    public void reportPostAbuse(MemberId memberId, TargetPostId targetPostId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue()).orElseThrow();
        PostEntity postEntity = postJpaRepository.findByUlid(targetPostId.getValue()).orElseThrow();
        postAbuRepJpaRepository.save(PostAbuseReportEntity.builder().member(memberEntity).post(postEntity).build());
    }

    @Override
    public void reportCommentAbuse(MemberId memberId, TargetCommentId targetCommentId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue()).orElseThrow();
        CommentEntity commentEntity = commentJpaRepository.findById(
                CommentCompositeKey.builder()
                        .post(targetCommentId.getTargetPostId().getValue())
                        .path(targetCommentId.getTargetCommentPath().getValue())
                        .build()
        ).orElseThrow();
        commentAbuseReportJpaRepository.save(
                CommentAbuseReportEntity.builder().member(memberEntity).comment(commentEntity).build());
    }

    @Override
    public void removeProposalOrBugReport(ReportId reportId) {
        deleteImageFromReportImagePath(reportId.getValue());
        processProposalOrBugReportRelatedRecords(reportId.getValue());
    }

    private void deleteImageFromReportImagePath(String reportId) {
        List<String> srcList = dsl.select(
                        field("jsonb_array_elements({0}) ->> 'src'", String.class, PROP_BUG_REP.IMAGE)
                )
                .from(PROP_BUG_REP)
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchInto(String.class);

        if (!srcList.isEmpty()) {
            applicationEventPublisher.publishEvent(ImageRemoveEvent.create(srcList));
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