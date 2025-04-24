package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberTermValidationService {
    private final SiteMemberTermRepository memberTermRepository;

    public void validateExistedMemberTermUuid(UUID uuid) {
        if (memberTermRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberTermEntity.class);
        }
    }

    public void validateNotFoundMemberTermUuid(UUID uuid) {
        if (uuid == null || memberTermRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberTermEntity.class);
        }
    }
}
