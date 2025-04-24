package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberValidationService {
    private final SiteMemberRepository memberRepository;

    public void validateExistedMemberUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberEntity.class);
        }
    }

    public void validateNotFoundMemberUuid(UUID uuid) {
        if (uuid == null || memberRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberEntity.class);
        }
    }
}
