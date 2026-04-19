package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.AbuseRepository;
import kr.modusplant.framework.jpa.repository.CommCommentAbuRepJpaRepository;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostAbuRepJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AbuseRepositoryJpaAdapter implements AbuseRepository {
    private final CommPostJpaRepository postJpaRepository;
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository;
    private final CommCommentJpaRepository commentJpaRepository;
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository;

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
