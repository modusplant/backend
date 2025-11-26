package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.framework.jpa.repository.CommPostBookmarkJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostLikeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TargetPostIdRepositoryJpaAdapter implements TargetPostIdRepository {
    private final CommPostJpaRepository postJpaRepository;
    private final CommPostLikeJpaRepository postLikeJpaRepository;
    private final CommPostBookmarkJpaRepository postBookmarkJpaRepository;

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
