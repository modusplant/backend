package kr.modusplant.domains.temp_like.temp_tip.domain.service;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.temp_like.temp_tip.persistence.repository.TipLikeRepository;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
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

    public boolean validateExistedTipLike(String tipPostId, UUID memberId) {
        return tipLikeRepository.existsByTipPostIdAndMemberId(tipPostId, memberId);
    }
}
