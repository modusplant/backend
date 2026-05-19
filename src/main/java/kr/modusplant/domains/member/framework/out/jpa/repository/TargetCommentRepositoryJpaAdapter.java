package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.framework.out.jpa.entity.CommentLikeEntity;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TargetCommentRepositoryJpaAdapter implements TargetCommentRepository {
    private final CommentJpaRepository commentJpaRepository;
    private final CommentLikeJpaRepository commentLikeJpaRepository;

    @Override
    public boolean isIdExist(TargetCommentId targetPostId) {
        return commentJpaRepository.existsByPostUlidAndPath(
                targetPostId.getTargetPostId().getValue(), targetPostId.getTargetCommentPath().getValue());
    }

    @Override
    public boolean isLiked(MemberId memberId, TargetCommentId targetPostId) {
        return commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(
                targetPostId.getTargetPostId().getValue(),
                targetPostId.getTargetCommentPath().getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, TargetCommentId targetPostId) {
        return !commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(
                targetPostId.getTargetPostId().getValue(),
                targetPostId.getTargetCommentPath().getValue(), memberId.getValue());
    }

    @Override
    public void like(MemberId memberId, TargetCommentId targetCommentId) {
        String postId = targetCommentId.getTargetPostId().getValue();
        String path = targetCommentId.getTargetCommentPath().getValue();

        commentLikeJpaRepository.save(CommentLikeEntity.of(postId, path, memberId.getValue()));
        commentJpaRepository.findByPostUlidAndPath(postId, path).orElseThrow().increaseLikeCount();
    }

    @Override
    public void unlike(MemberId memberId, TargetCommentId targetCommentId) {
        String postId = targetCommentId.getTargetPostId().getValue();
        String path = targetCommentId.getTargetCommentPath().getValue();

        commentLikeJpaRepository.delete(CommentLikeEntity.of(postId, path, memberId.getValue()));
        commentJpaRepository.findByPostUlidAndPath(postId, path).orElseThrow().decreaseLikeCount();
    }
}