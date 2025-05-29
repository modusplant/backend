package kr.modusplant.domains.communication.tip.domain.service.like;

import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.communication.tip.persistence.like.repository.TipLikeRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipLikeValidationService {
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository memberRepository;
    private final TipLikeRepository tipLikeRepository;

    public void validateExistedTipPostAndMember(String tipPostId, UUID memberId) {
        if (tipPostId == null || memberId == null) {
            throw new IllegalArgumentException("tipPostId and memberId must not be null");
        };

        if (!tipPostRepository.existsById(tipPostId)) {
            throw new EntityNotFoundWithUlidException(tipPostId, TipPostEntity.class);
        }
        if (!memberRepository.existsById(memberId)) {
            throw new EntityExistsWithUuidException(memberId, SiteMemberEntity.class);
        }
    }

    public void validateTipLikeExists(String tipPostId, UUID memberId) {
        if (!tipLikeRepository.existsByTipPostIdAndMemberId(tipPostId, memberId)) {
            throw new IllegalArgumentException("member not liked status");
        }
    }

    public void validateTipLikeNotExists(String tipPostId, UUID memberId) {
        if (tipLikeRepository.existsByTipPostIdAndMemberId(tipPostId, memberId)) {
            throw new IllegalArgumentException("member already liked");
        }
    }
}
