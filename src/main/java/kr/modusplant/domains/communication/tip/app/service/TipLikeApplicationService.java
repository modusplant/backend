package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.tip.domain.service.TipLikeValidationService;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipLikeRepository;
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
    public LikeResponse likeTipPost(String postId, UUID memberId) {
        tipLikeValidationService.validateExistedTipPostAndMember(postId, memberId);
        tipLikeValidationService.validateExistedTipLike(postId, memberId);

        TipPostEntity tipPost = tipPostRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        tipPost.increaseLikeCount();

        tipLikeRepository.save(TipLikeEntity.of(postId, memberId));
        return LikeResponse.of(tipPost.getLikeCount(), true);
    }

    @Transactional
    public LikeResponse unlikeTipPost(String postId, UUID memberId) {
        tipLikeValidationService.validateExistedTipPostAndMember(postId, memberId);
        tipLikeValidationService.validateNotFoundTipLike(postId, memberId);

        TipPostEntity tipPost = tipPostRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        tipPost.decreaseLikeCount();

        tipLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        return LikeResponse.of(tipPost.getLikeCount(), false);
    }
}
