package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostRepository;
import kr.modusplant.framework.jpa.repository.PostBookmarkJpaRepository;
import kr.modusplant.framework.jpa.repository.PostJpaRepository;
import kr.modusplant.framework.jpa.repository.PostLikeJpaRepository;
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
}
