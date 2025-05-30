package kr.modusplant.domains.communication.tip.app.service.like;

import kr.modusplant.domains.communication.tip.app.http.like.response.TipLikeResponse;
import kr.modusplant.domains.communication.tip.domain.service.like.TipLikeValidationService;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.like.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.like.repository.TipLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipLikeApplicationService {
    private final TipPostRepository tipPostRepository;
    private final TipLikeRepository tipLikeRepository;
    private final TipLikeValidationService tipLikeValidationService;

    @Transactional
    public TipLikeResponse likeTipPost(String tipPostId, UUID memberId) {
        tipLikeValidationService.validateExistedTipPostAndMember(tipPostId, memberId);
        tipLikeValidationService.validateTipLikeNotExists(tipPostId, memberId);

        TipPostEntity tipPost = tipPostRepository.findById(tipPostId).orElseThrow(() -> new IllegalArgumentException("tip post not found"));
        tipPost.increaseLikeCount();

        tipLikeRepository.save(TipLikeEntity.of(tipPostId, memberId));
        return TipLikeResponse.of(tipPost.getLikeCount(), true);
    }

    @Transactional
    public TipLikeResponse unlikeTipPost(String tipPostId, UUID memberId) {
        tipLikeValidationService.validateExistedTipPostAndMember(tipPostId, memberId);
        tipLikeValidationService.validateTipLikeExists(tipPostId, memberId);

        TipPostEntity tipPost = tipPostRepository.findById(tipPostId).orElseThrow(() -> new IllegalArgumentException("tip post not found"));
        tipPost.decreaseLikeCount();

        tipLikeRepository.deleteByTipPostIdAndMemberId(tipPostId, memberId);
        return TipLikeResponse.of(tipPost.getLikeCount(), false);
    }
}
