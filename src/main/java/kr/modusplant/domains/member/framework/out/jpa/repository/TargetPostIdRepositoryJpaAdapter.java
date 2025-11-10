package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TargetPostIdRepositoryJpaAdapter implements TargetPostIdRepository {
    private final CommPostJpaRepository commPostJpaRepository;
    private final CommPostLikeJpaRepository commPostLikeJpaRepository;

    @Override
    public boolean isIdExist(TargetPostId targetPostId) {
        return commPostJpaRepository.existsByUlid(targetPostId.getValue());
    }

    @Override
    public boolean isPublished(TargetPostId targetPostId) {
        return commPostJpaRepository.findByUlid(targetPostId.getValue()).orElseThrow().getIsPublished().equals(true);
    }

    @Override
    public boolean isLiked(MemberId memberId, TargetPostId targetPostId) {
        return commPostLikeJpaRepository.existsByPostIdAndMemberId(targetPostId.getValue(), memberId.getValue());
    }

    @Override
    public boolean isUnliked(MemberId memberId, TargetPostId targetPostId) {
        return !commPostLikeJpaRepository.existsByPostIdAndMemberId(targetPostId.getValue(), memberId.getValue());
    }
}
