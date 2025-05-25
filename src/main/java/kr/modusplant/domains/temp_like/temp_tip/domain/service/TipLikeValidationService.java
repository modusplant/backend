package kr.modusplant.domains.temp_like.temp_tip.domain.service;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipLikeValidationService {
    private final SiteMemberRepository memberRepository;

    public void validateExistedTipPostAndMember(String tipPostId, UUID memberId) {
        if (tipPostId == null || memberId == null) return;

        // TODO : TipPost 존재여부 검증

        if (memberRepository.findByUuid(memberId).isPresent()) {
            throw new EntityExistsWithUuidException(memberId, SiteMemberEntity.class);
        }
    }
}
