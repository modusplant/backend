package kr.modusplant.domains.temp_like.temp_tip.app.service;

import kr.modusplant.domains.temp_like.temp_tip.domain.service.TipLikeValidationService;
import kr.modusplant.domains.temp_like.temp_tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.temp_like.temp_tip.persistence.repository.TipLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipLikeApplicationService {
    private final TipLikeRepository tipLikeRepository;
    private final TipLikeValidationService tipLikeValidationService;

    public void likeTipPost(String tipPostId, UUID memberId) {
        tipLikeValidationService.validateExistedTipPostAndMember(tipPostId, memberId);
        boolean tipLikeStatus = tipLikeValidationService.validateExistedTipLike(tipPostId, memberId);
        if (tipLikeStatus) { throw new IllegalArgumentException("already liked"); }
        tipLikeRepository.save(TipLikeEntity.of(tipPostId, memberId));
    }

    public void unlikeTipPost(String tipPostId, UUID memberId) {
        tipLikeValidationService.validateExistedTipPostAndMember(tipPostId, memberId);
        boolean tipLikeStatus = tipLikeValidationService.validateExistedTipLike(tipPostId, memberId);
        if (!tipLikeStatus) { throw new IllegalArgumentException("not liked status"); }
        tipLikeRepository.delete(TipLikeEntity.of(tipPostId, memberId));
    }
}
