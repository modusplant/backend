package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TargetCommentRepositoryJpaAdapter implements TargetCommentRepository {
    private final CommentJpaRepository postJpaRepository;
    private final CommentLikeJpaRepository postLikeJpaRepository;

    @Override
    public boolean isIdExist(TargetCommentId targetPostId) {
        return postJpaRepository.existsByPostUlidAndPath(
                targetPostId.getTargetPostId().getValue(), targetPostId.getTargetCommentPath().getValue());
    }

    @Override
    public boolean isLiked(MemberId memberId, TargetCommentId targetPostId) {
        return postLikeJpaRepository.existsByPostIdAndPathAndMemberId(
                targetPostId.getTargetPostId().getValue(),
                targetPostId.getTargetCommentPath().getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, TargetCommentId targetPostId) {
        return !postLikeJpaRepository.existsByPostIdAndPathAndMemberId(
                targetPostId.getTargetPostId().getValue(),
                targetPostId.getTargetCommentPath().getValue(), memberId.getValue());
    }
}
