package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryJpaAdapter implements ReportRepository {
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
}
