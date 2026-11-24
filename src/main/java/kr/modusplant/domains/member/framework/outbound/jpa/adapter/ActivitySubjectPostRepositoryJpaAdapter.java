package kr.modusplant.domains.member.framework.outbound.jpa.adapter;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostBookmarkJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostLikeJpaRepository;
import kr.modusplant.domains.member.usecase.port.repository.ActivitySubjectPostRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.enums.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.NOT_FOUND_POST;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectPostRepositoryJpaAdapter implements ActivitySubjectPostRepository {
    private final PostJpaRepository postJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostBookmarkJpaRepository postBookmarkJpaRepository;

    @Override
    public boolean isIdExist(ActivitySubjectPostId activitySubjectPostId) {
        return postJpaRepository.existsByUlid(activitySubjectPostId.getValue());
    }

    @Override
    public boolean isPublished(ActivitySubjectPostId activitySubjectPostId) {
        return postJpaRepository.findByUlid(activitySubjectPostId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_POST, "post"))
                .getIsPublished().equals(true);
    }

    @Override
    public boolean isInQnaPrimaryCategory(ActivitySubjectPostId activitySubjectPostId) {
        PrimaryCategoryEntity primaryCategory = postJpaRepository.findByUlid(activitySubjectPostId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_POST, "post"))
                .getPrimaryCategory();
        return primaryCategory != null && PostType.QNA.getValue().equals(primaryCategory.getCategory());
    }

    @Override
    public boolean isLiked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        return postLikeJpaRepository.existsByPostIdAndMemberId(activitySubjectPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        return !postLikeJpaRepository.existsByPostIdAndMemberId(activitySubjectPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isBookmarked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        return postBookmarkJpaRepository.existsByPostIdAndMemberId(activitySubjectPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isNotBookmarked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        return !postBookmarkJpaRepository.existsByPostIdAndMemberId(activitySubjectPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isWrittenByMember(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        return postJpaRepository.findByUlid(activitySubjectPostId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_POST, "post"))
                .getAuthMember().getUuid().equals(memberId.getValue());
    }

    @Override
    public void like(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        String postId = activitySubjectPostId.getValue();

        postLikeJpaRepository.save(PostLikeEntity.of(postId, memberId.getValue()));
        postJpaRepository.findByUlid(postId)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_POST, "post"))
                .increaseLikeCount();
    }

    @Override
    public void unlike(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        String postId = activitySubjectPostId.getValue();

        postLikeJpaRepository.delete(PostLikeEntity.of(postId, memberId.getValue()));
        postJpaRepository.findByUlid(postId)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_POST, "post"))
                .decreaseLikeCount();
    }

    @Override
    public void bookmark(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        postBookmarkJpaRepository.save(PostBookmarkEntity.of(activitySubjectPostId.getValue(), memberId.getValue()));
    }

    @Override
    public void cancelBookmark(MemberId memberId, ActivitySubjectPostId activitySubjectPostId) {
        postBookmarkJpaRepository.delete(PostBookmarkEntity.of(activitySubjectPostId.getValue(), memberId.getValue()));
    }
}
