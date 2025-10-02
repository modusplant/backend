package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.framework.out.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommCommentLikeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TargetCommentIdRepositoryJpaAdapter implements TargetCommentIdRepository {
    private final CommCommentJpaRepository commPostJpaRepository;
    private final CommCommentLikeJpaRepository commPostLikeJpaRepository;

    @Override
    public boolean isIdExist(TargetCommentId targetPostId) {
        return commPostJpaRepository.existsByPostUlidAndPath(targetPostId.getTargetPostId().getValue(), targetPostId.getTargetCommentPath().getValue());
    }

    @Override
    public boolean isLiked(MemberId memberId, TargetCommentId targetPostId) {
        return commPostLikeJpaRepository.existsByPostIdAndPathAndMemberId(targetPostId.getTargetPostId().getValue(), targetPostId.getTargetCommentPath().getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, TargetCommentId targetPostId) {
        return !commPostLikeJpaRepository.existsByPostIdAndPathAndMemberId(targetPostId.getTargetPostId().getValue(), targetPostId.getTargetCommentPath().getValue(), memberId.getValue());
    }
}
