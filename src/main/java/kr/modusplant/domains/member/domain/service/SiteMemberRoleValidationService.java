package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleCrudJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberRoleValidationService {
    private final SiteMemberRoleCrudJpaRepository memberRoleRepository;

    public void validateExistedMemberRoleUuid(UUID uuid) {
        if (memberRoleRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberRoleEntity.class);
        }
    }

    public void validateNotFoundMemberRoleUuid(UUID uuid) {
        if (uuid == null || memberRoleRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberRoleEntity.class);
        }
    }
}
