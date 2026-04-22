package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.framework.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryJpaAdapter implements ReportRepository {
    private final PropBugRepJpaRepository propBugRepJpaRepository;
    private final CommPostJpaRepository postJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;
    private final CommCommentJpaRepository commentJpaRepository;
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository;

    @Override
    public boolean isIdExist(ReportId reportId) {
        return propBugRepJpaRepository.existsByUlid(reportId.getValue());
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
        return commentAbuRepJpaRepository.findByMemberIdAndComment(
                memberId.getValue(),
                commentJpaRepository.findByPostUlidAndPath(
                        targetCommentId.getTargetPostId().getValue(),
                        targetCommentId.getTargetCommentPath().getValue()).orElseThrow()
        ).isPresent();
    }
}
