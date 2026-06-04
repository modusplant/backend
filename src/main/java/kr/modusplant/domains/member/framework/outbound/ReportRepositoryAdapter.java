package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.record.FilenameAndSrcEntityRecord;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.CommentAbuseReportJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepository {
    private final ApplicationEventPublisher applicationEventPublisher;

    private final MemberJpaRepository memberJpaRepository;
    private final ProposalOrBugReportJpaRepository proposalOrBugReportJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final PostAbuseReportJpaRepository postAbuseReportJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentAbuseReportJpaRepository commentAbuseReportJpaRepository;

    private final ProposalOrBugReportJooqRepository proposalOrBugReportJooqRepository;

    @Override
    public boolean isIdExistInProposalOrBugReport(ReportId reportId) {
        return proposalOrBugReportJpaRepository.existsByUlid(reportId.getValue());
    }

    @Override
    public boolean isUncheckedInProposalOrBugReport(ReportId reportId) {
        return proposalOrBugReportJpaRepository.findByUlid(reportId.getValue()).orElseThrow().getCheckedAt() == null;
    }

    @Override
    public boolean isCheckedInProposalOrBugReport(ReportId reportId) {
        return proposalOrBugReportJpaRepository.findByUlid(reportId.getValue()).orElseThrow().getCheckedAt() != null;
    }

    @Override
    public boolean isMemberAbusePost(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        return postAbuseReportJpaRepository.findByMemberIdAndPost(
                memberId.getValue(),
                postJpaRepository.findByUlid(activitySubjectPostId.getValue()).orElseThrow()
        ).isPresent();
    }

    @Override
    public boolean isMemberAbuseComment(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId) {
        return commentAbuseReportJpaRepository.findByMemberIdAndComment(
                memberId.getValue(),
                commentJpaRepository.findByPostUlidAndPath(
                        activitySubjectCommentId.getActivitySubjectPostId().getValue(),
                        activitySubjectCommentId.getActivitySubjectCommentPath().getValue()).orElseThrow()
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
        proposalOrBugReportJpaRepository.save(
                ProposalOrBugReportEntity.builder()
                        .ulid(reportId)
                        .member(memberEntity)
                        .title(title)
                        .content(content)
                        .image(imageList)
                        .build());
    }

    @Override
    public ReportTime reportPostAbuse(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue()).orElseThrow();
        PostEntity postEntity = postJpaRepository.findByUlid(activitySubjectPostId.getValue()).orElseThrow();
        PostAbuseReportEntity reportEntity = PostAbuseReportEntity.builder().member(memberEntity).post(postEntity).build();
        return ReportTime.create(postAbuseReportJpaRepository.save(reportEntity).getCreatedAt());
    }

    @Override
    public ReportTime reportCommentAbuse(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue()).orElseThrow();
        CommentEntity commentEntity = commentJpaRepository.findById(
                CommentCompositeKey.builder()
                        .post(activitySubjectCommentId.getActivitySubjectPostId().getValue())
                        .path(activitySubjectCommentId.getActivitySubjectCommentPath().getValue())
                        .build()
        ).orElseThrow();
        return ReportTime.create(
                commentAbuseReportJpaRepository.save(
                                CommentAbuseReportEntity.builder()
                                        .member(memberEntity)
                                        .comment(commentEntity)
                                        .build())
                        .getCreatedAt());
    }

    @Override
    public void removeProposalOrBugReport(ReportId reportIdVo) {
        String reportId = reportIdVo.getValue();
        List<String> srcList = proposalOrBugReportJooqRepository.getImageFileKeysFromReportId(reportId);
        if (!srcList.isEmpty()) {
            applicationEventPublisher.publishEvent(ImagesRemoveTask.create(srcList));
        }
        proposalOrBugReportJooqRepository.archiveByReportId(reportId);
        proposalOrBugReportJpaRepository.deleteByUlid(reportId);
    }
}