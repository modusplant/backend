package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentLikeEntity;
import kr.modusplant.domains.member.usecase.port.repository.ActivitySubjectCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectCommentRepositoryJpaAdapter implements ActivitySubjectCommentRepository {
    private final CommentJpaRepository commentJpaRepository;
    private final CommentLikeJpaRepository commentLikeJpaRepository;

    @Override
    public boolean isIdExist(ActivitySubjectCommentId activitySubjectPostId) {
        return commentJpaRepository.existsByPostUlidAndPath(
                activitySubjectPostId.getActivitySubjectPostId().getValue(), activitySubjectPostId.getActivitySubjectCommentPath().getValue());
    }

    @Override
    public boolean isLiked(MemberId memberId, ActivitySubjectCommentId activitySubjectPostId) {
        return commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(
                activitySubjectPostId.getActivitySubjectPostId().getValue(),
                activitySubjectPostId.getActivitySubjectCommentPath().getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, ActivitySubjectCommentId activitySubjectPostId) {
        return !commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(
                activitySubjectPostId.getActivitySubjectPostId().getValue(),
                activitySubjectPostId.getActivitySubjectCommentPath().getValue(), memberId.getValue());
    }

    @Override
    public void like(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId) {
        String postId = activitySubjectCommentId.getActivitySubjectPostId().getValue();
        String path = activitySubjectCommentId.getActivitySubjectCommentPath().getValue();

        commentLikeJpaRepository.save(CommentLikeEntity.of(postId, path, memberId.getValue()));
        commentJpaRepository.findByPostUlidAndPath(postId, path).orElseThrow().increaseLikeCount();
    }

    @Override
    public void unlike(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId) {
        String postId = activitySubjectCommentId.getActivitySubjectPostId().getValue();
        String path = activitySubjectCommentId.getActivitySubjectCommentPath().getValue();

        commentLikeJpaRepository.delete(CommentLikeEntity.of(postId, path, memberId.getValue()));
        commentJpaRepository.findByPostUlidAndPath(postId, path).orElseThrow().decreaseLikeCount();
    }
}