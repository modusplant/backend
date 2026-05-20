package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TargetPostRepositoryJpaAdapter implements TargetPostRepository {
    private final PostJpaRepository postJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostBookmarkJpaRepository postBookmarkJpaRepository;

    @Override
    public boolean isIdExist(TargetPostId targetPostId) {
        return postJpaRepository.existsByUlid(targetPostId.getValue());
    }

    @Override
    public boolean isPublished(TargetPostId targetPostId) {
        return postJpaRepository.findByUlid(targetPostId.getValue()).orElseThrow().getIsPublished().equals(true);
    }

    @Override
    public boolean isLiked(MemberId memberId, TargetPostId targetPostId) {
        return postLikeJpaRepository.existsByPostIdAndMemberId(targetPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, TargetPostId targetPostId) {
        return !postLikeJpaRepository.existsByPostIdAndMemberId(targetPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isBookmarked(MemberId memberId, TargetPostId targetPostId) {
        return postBookmarkJpaRepository.existsByPostIdAndMemberId(targetPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isNotBookmarked(MemberId memberId, TargetPostId targetPostId) {
        return !postBookmarkJpaRepository.existsByPostIdAndMemberId(targetPostId.getValue(), memberId.getValue());
    }

    @Override
    public void like(MemberId memberId, TargetPostId targetPostId) {
        String postId = targetPostId.getValue();

        postLikeJpaRepository.save(PostLikeEntity.of(postId, memberId.getValue()));
        postJpaRepository.findByUlid(postId).orElseThrow().increaseLikeCount();
    }

    @Override
    public void unlike(MemberId memberId, TargetPostId targetPostId) {
        String postId = targetPostId.getValue();

        postLikeJpaRepository.delete(PostLikeEntity.of(postId, memberId.getValue()));
        postJpaRepository.findByUlid(postId).orElseThrow().decreaseLikeCount();
    }

    @Override
    public void bookmark(MemberId memberId, TargetPostId targetPostId) {
        postBookmarkJpaRepository.save(PostBookmarkEntity.of(targetPostId.getValue(), memberId.getValue()));
    }

    @Override
    public void cancelBookmark(MemberId memberId, TargetPostId targetPostId) {
        postBookmarkJpaRepository.delete(PostBookmarkEntity.of(targetPostId.getValue(), memberId.getValue()));
    }
}
